package com.mcal.pocketinveditor.entity;

public class PlayerAbilities {
    public float flySpeed = 0.05f;
    public boolean flying = false;
    public boolean instabuild = false;
    public boolean invulnerable = false;
    public boolean lightning = false;
    public boolean mayFly = false;
    public float walkSpeed = 0.1f;

    public void initForGameType(int gameType) {
        boolean creative;
        boolean z = true;
        if (gameType == 1) {
            creative = true;
        } else {
            creative = false;
        }
        invulnerable = creative;
        instabuild = creative;
        mayFly = creative;
        if (!(flying && creative)) {
            z = false;
        }
        this.flying = z;
    }
}
