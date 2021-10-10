package com.company;

/**
 * @Description:
 * @author: Administrator
 * @Date: 2021/8/25
 */
public class SwitchTest {


    public static void main(String[] args) {
        double dob = 1.0;
        long lob=1;
        float fob=1;
        String swstr = "test";

        switch (swstr) {
            case "test":
                System.out.println("test");
                break;
            case "test1":
                System.out.println("test1");
                break;
            default:
                System.out.println("null");
        }

        final String s = "abc";
        String x = "abc" + "def";
        String y = s + "def";
        String z = x + "abc";
        String z1 = s + "def" + "abc";


        String s1 = "black";
        String s2 = "board";
        String s3 = s1 + s2;
        String s4 = "black" + s2;
        System.out.println("s3==s4 " + (s3 == s4));
        System.out.println(s4.intern() == s3.intern());

        //
        String s5 = 3 + 4 + "5";
        String s6 = "3" + 4 + 5;
        System.out.println(s5);
        System.out.println(s6);
        nullCall();

        ParentX parentX = new SubToY();
    }

    public static void nullCall() {
        Object o = new Object();
        NullCall nullCall = null;
        nullCall.m();
    }
}

class NullCall {
    public static void m() {
        System.out.println("m()");
    }
}


class ParentX {
    public String kind = "parent";

    @Override
    public String toString() {

        return kind;
    }

    public ParentX() {
        System.out.println(toString());
    }
}

class SubToY extends ParentX {
    public String color = "sub";
    public String kind = "sub";

    @Override
    public String toString() {

        return "super.kind = " + super.toString() + " ,this.color=" + color + " ,this.kind=" + kind;
    }


}
