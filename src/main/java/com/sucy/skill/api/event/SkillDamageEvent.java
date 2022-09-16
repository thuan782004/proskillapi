/**
 * SkillAPI
 * com.sucy.skill.api.event.SkillDamageEvent
 * <p>
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2014 Steven Sucy
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software") to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.sucy.skill.api.event;

import com.sucy.skill.api.skills.Skill;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * An event for when an entity is damaged by
 * another entity with the use of a skill.
 */
public class SkillDamageEvent extends DamageEvent implements Cancellable {
    @Getter private final String       classification;
    @Getter private final Skill        skill;
    @Getter
    @Setter
    private               boolean      knockback;

    /**
     * Initializes a new event
     *
     * @param skill          skill used to deal damage
     * @param damager        entity dealing the damage
     * @param target         entity receiving the damage
     * @param damage         the amount of damage dealt
     * @param classification classification of damage
     */
    public SkillDamageEvent(Skill skill, LivingEntity damager, LivingEntity target, double damage,
                            String classification, boolean knockback) {
        super(damager, target, damage);
        this.skill = skill;
        this.classification = classification;
        this.knockback = knockback;
    }
}
