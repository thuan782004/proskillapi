package com.sucy.tunnel.book;

import com.sucy.skill.SkillAPI;
import com.sucy.tunnel.book.internals.BookButton;
import com.sucy.tunnel.book.variable.ActiveSkillVar;
import com.sucy.tunnel.book.variable.BookVariable;
import com.sucy.tunnel.book.variable.PassiveSkillVar;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BookLoader {

    public static final BookLoader ins = new BookLoader(SkillAPI.inst());

    public static HashMap<String, Class<? extends  BookVariable>> variables = new HashMap<>();
    static {
        variables.put("active_skill", ActiveSkillVar.class);
        variables.put("passive_skill", PassiveSkillVar.class);
    }

    public static ItemStack parse(Player p, Plugin plugin, String path){
        return parse(p,new File(plugin.getDataFolder(),path));
    }

    public static ItemStack parse(Player p, File f){
        return parse(p,YamlConfiguration.loadConfiguration(f));
    }

    public static ItemStack parse(Player p,YamlConfiguration c){
        Set<String> keys = c.getKeys(false);
        List<BaseComponent[]> pages = new ArrayList<>();
        keys.forEach(k -> {
            if (k.endsWith("_dyn"))
                pages.addAll(new DynamicPageLoader(p, c.getStringList(k)).build());
            else
                pages.add(new StaticPageLoader(p, c.getStringList(k)).build());
        });
        return BookUtil.writtenBook().author("funayd").pages(pages).build();
    }

    private static class DynamicPageLoader {
        private final Player player;
        private final List<String> source;
        private final HashMap<String,BookVariable> var = new HashMap<>();
        private final List<BaseComponent[]> pages = new ArrayList<>();
        public DynamicPageLoader(Player p, List<String> stringList) {
            this.player = p;
            this.source = stringList;
        }

        public List<BaseComponent[]> build() {
            Pattern pattern = Pattern.compile("(?<=\\{).*(?=})");
            this.source.parallelStream().forEach(s -> {
                Matcher m = pattern.matcher(s);
                while (m.find()){
                    String d = m.group();
                    if (!variables.containsKey(d) || var.containsKey(d)) continue;
                    try { var.put(d,variables.get(d).getConstructor(OfflinePlayer.class).newInstance(this.player));
                    } catch ( NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e ) {
                        e.printStackTrace();
                    }
                }
                });
            List<List<String>> sourcePages = new ArrayList<>();
            while (var.values().parallelStream().anyMatch(BookVariable::isContinue)){
                List<String> temp = new ArrayList<>();
                this.source.forEach(line -> var.forEach((k, v)-> {
                    if (line.contains("{" + k + "}")) temp.add(line.replace("{" + k + "}",v.next() ));
                    else temp.add(line);
                }));
                sourcePages.add(temp);
            }

            sourcePages.forEach(p -> pages.add(new StaticPageLoader(this.player,p).build()));
            return pages;
        }
    }
    private static class StaticPageLoader {
        boolean placeholder = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
        BookUtil.PageBuilder builder;
        private StaticPageLoader(Player p, List<String> source) {
            builder = new BookUtil.PageBuilder();
            source.forEach(s -> {
                if (placeholder)
                s = PlaceholderAPI.setPlaceholders(p, s);
                builder.add(new LineLoader(s).build());
                builder.newLine();
            });
        }

        private BaseComponent[] build(){
            return builder.build();
        }
    }
    private static class LineLoader {

        Collection<BaseComponent> parts;
        private LineLoader(String source){
            parts = new LinePatten(source).parts;
        }
        private Collection<BaseComponent> build(){
            return parts;
        }

    }
    private static class LinePatten {

        private static final String h = "(hover\\[([^]]+|)])";
        private static final String c = "((link|run|page|copy|suggest|button)\\[([^]]+|)])";
        private static final Pattern parse =        Pattern.compile("\\[[^\\[]+]("+h+c+"|"+h+"|"+c+")");
        private static final Pattern clickable =    Pattern.compile("\\[[^\\[]+]("+h+"|)"+c);
        private static final Pattern hoverable =    Pattern.compile("\\[[^\\[]+]"+h);
        private static final Pattern text =         Pattern.compile("(?<=\\[)[^\\[]+(?=])");
        private static final Pattern hoverContent = Pattern.compile("(?<=hover\\[)([^]]+|)(?=])");
        private static final Pattern clickContent = Pattern.compile("(?<=(link|run|page|copy|suggest|button)\\[)([^]]+|)(?=])");
        private static final Pattern clickType =    Pattern.compile("(?<=])(link|run|page|copy|suggest|button)(?=\\[([^]]+|)])");

        private final List<BaseComponent> parts = new ArrayList<>();
        private LinePatten(String source){
            int cursor = 0;
            if (!parse.matcher(source).find()){
                Collections.addAll(parts, TextComponent.fromLegacyText(
                        ChatColor.translateAlternateColorCodes('&',source)
                ));
                return;
            }
            Matcher matcher = parse.matcher(source);
            while (matcher.find()){
                if (cursor<matcher.start()) {
                    Collections.addAll(parts, TextComponent.fromLegacyText(
                            ChatColor.translateAlternateColorCodes('&',
                                    source.substring(cursor, matcher.start())
                            )
                    ));
                    cursor = matcher.end();
                }
                String target = matcher.group();
                ComponentBuilder tb = new ComponentBuilder(ChatColor.translateAlternateColorCodes('&',getText(target)));
                if (isHoverable(target)) {
                    tb.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                            new Text(TextComponent.fromLegacyText(
                                    ChatColor.translateAlternateColorCodes('&',
                                            getHover(target)
                                    )
                            )
                    )));
                }
                if (isClickable(target)){
                    ClickEvent.Action action = getClickType(target);
                    if (action==null) {
                        BookButton.parse(tb,getClick(target));
                    }
                    else tb.event(new ClickEvent(action,getClick(target)));
                }
                Collections.addAll(parts, tb.create());
            }
            if (cursor<source.length()){
                Collections.addAll(parts, TextComponent.fromLegacyText(
                        ChatColor.translateAlternateColorCodes('&',
                                source.substring(cursor)
                        )
                ));
            }
        }

        private boolean isClickable(String a){
            return clickable.matcher(a).find();
        }
        private boolean isHoverable(String a){
            return hoverable.matcher(a).find();
        }
        private String getText(String a){
            Matcher m = text.matcher(a);
            if (m.find()) return m.group();
            else return "";
        }
        private String getHover(String a){
            Matcher m = hoverContent.matcher(a);
            if (m.find()) return m.group();
            else return "";
        }
        private String getClick(String a){
            Matcher m = clickContent.matcher(a);
            if (m.find()) return m.group();
            else return "";
        }
        private ClickEvent.Action getClickType(String a){
            Matcher m = clickType.matcher(a);
            if (m.find())
                switch (m.group()){
                    case "link": return ClickEvent.Action.OPEN_URL;
                    case "page": return ClickEvent.Action.CHANGE_PAGE;
                    case "copy": return ClickEvent.Action.COPY_TO_CLIPBOARD;
                    case "suggest": return ClickEvent.Action.SUGGEST_COMMAND;
                    case "button": return null;
                    default: return ClickEvent.Action.RUN_COMMAND;
            }
            return ClickEvent.Action.RUN_COMMAND;
        }
    }

    public final HashMap<String,YamlConfiguration> data = new HashMap<>();
    private BookLoader(Plugin p){
        reload(p);
    }
    public void reload(Plugin p){
        data.clear();
        File folder = new File(p.getDataFolder(),"book");
        if (!folder.exists() && folder.mkdirs()){
            p.saveResource("book/main.yml",false);
            p.saveResource("book/ability.yml",false);
        }
        if (Objects.requireNonNull(folder.list()).length!=0) {
            Arrays.stream(Objects.requireNonNull(folder.list()))
                    .parallel().filter(n -> n.endsWith(".yml"))
                    .forEach(n -> {
                        File file = new File(p.getDataFolder(), "book/" + n);
                        YamlConfiguration cf = YamlConfiguration.loadConfiguration(file);
                        data.put(n.replace(".yml", ""), cf);
                        p.getServer().getConsoleSender().sendMessage("Loaded " + n);
                    });
        }
    }
    public boolean open(Player p,String id){
        if (!data.containsKey(id)) {
            p.sendMessage("Không tìm thấy sách tên "+id);
            return false;
        }
        p.openBook(parse(p,data.get(id)));
        return true;
    }
}
