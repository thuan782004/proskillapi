package com.sucy.tunnel.book.internals;

import com.sucy.skill.SkillAPI;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Random;
import java.util.function.Consumer;

public class BookButton {

    private static final HashMap<String,Consumer<Player>> task = new HashMap<>();
    private static final HashMap<String,String> ocbTask = new HashMap<>();
    private static String random(String prefix){
        StringBuilder rd = new StringBuilder(prefix+"-");  //tạo cơ sở cho chuỗi <prefix>
        for (int i=0;i<10;i++) {             //tiến trình này lặp lại 8 lần cho ra 8 ký tự ngẫu nhiên
            Random random = new Random();           //tạo hàm ngẫu nhiên mới
            int upcase = 48;                        //giá trị mặc định cho giới hạn trên  kí tự 0
            int lowcase = 58;                       //giá trị mặc định cho giới hạn dưới  ký tự 9
            int ranfom = random.nextInt(3);  //Tạo số ngẫu nhiên từ 0-2
            switch (ranfom){
                case 1:                             //nếu là 1
                    upcase = 65;                    //giá trị cho giới hạn trên  kí tự A
                    lowcase = 91;                   //giá trị cho giới hạn trên  kí tự Z
                    break;
                case 2:
                    upcase = 97;                    //giá trị cho giới hạn trên  kí tự a
                    lowcase = 123;                  //giá trị cho giới hạn trên  kí tự z
                    break;
            }
            Random random1 = new Random();      //tạo hàm ngẫu nhiên thứ 2
            rd.append(random1.ints(upcase, lowcase)        //lấy ký tự ngẫu nhiên trong khoảng giới hạn
                    .limit(1)                              //chỉ lấy 1 ký tự
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString());
        }
        return rd.toString();               //xuất ra thành chuỗi
    }
    private static String generate(String id){
        String ocb = random(id);
        ocbTask.put(ocb,id);
        Bukkit.getScheduler().scheduleSyncDelayedTask(SkillAPI.inst(),()->{
            ocbTask.remove(ocb);
        },5*60*1000);
        return ocb;
    }
    public static boolean execute(Player p, String ocb){
        if (ocbTask.containsKey(ocb) && task.containsKey(ocbTask.get(ocb))){
            task.get(ocbTask.get(ocb)).accept(p);
        }
        return true;
    }
    public static void parse(ComponentBuilder tb, String id) {
        String ocb = generate(id);
        tb.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/tunnel ocb "+ocb));
    }
    public static void register(String id, Consumer<Player> task){
        BookButton.task.put(id,task);
    }
}
