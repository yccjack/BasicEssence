package com.mystical.basicessence.base;

/**
 * @Description:
 * @author: Administrator
 * @Date: 2021/8/27
 */
public class Test {
    private int x;
    private int y = 20;
    private static int staticx;
    private static int staticy = 20;

    {
        x = 20;
        if (y > 20) {
            throw new Exception();
        }
        //return;
    }

    static {
        staticx = 30;
        if (staticy > 20) {
            //throw new Exception();
        }
        //return;
    }

    public Test() throws Exception {
        this(0, 0);
    }

    public Test(int x, int y) throws Exception {

    }

    public static void main(String[] args) {
        Object o = new Object();
        NullCall2 nullCall = null;
        nullCall.m();


        NullCall2 nullCall2 = new NullCallSub();
        NullCall2 nullCall3 = new NullCall2();
        nullCall3.noStatic();
        nullCall3.noStatic(1);
        //对象方法
        //静态分派，动态分派。
        nullCall2.noStatic();


        int sp1 = 1;
        int sp2 = ++sp1;
        System.out.println(sp1);
        System.out.println(sp2);

        int sd1 = 1;

        sd1 = sd1++;
        System.out.println(sd1);
    }
}

class NullCall2 {
    public static void m() {
        System.out.println("m()");
    }

    public void noStatic() {
        System.out.println("noStatic()");
    }
    public void noStatic(int i) {
        System.out.println("i noStatic()");
    }
}


class NullCallSub extends NullCall2{


    public static void m() {
        System.out.println("sub m()");
    }
    @Override
    public void noStatic() {
        System.out.println("sub noStatic()");
    }
    @Override
    public void noStatic(int i) {
        System.out.println("sub i noStatic()");
    }
}


