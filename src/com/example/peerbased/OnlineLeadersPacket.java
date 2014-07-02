package com.example.peerbased;

import java.io.Serializable;

public class OnlineLeadersPacket implements Serializable {
    
    public static final long serialVersionUID = 537L;
    
    public String[] leaders;
    
    public OnlineLeadersPacket(String[] leaders)
    {
        this.leaders = leaders;
    }
}
