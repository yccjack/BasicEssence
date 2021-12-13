package com.mystical.basicessence.base;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) throws InterruptedException {


        final short s = -30;
        byte b = s;
        System.out.println(b);

        char a = (char) b;
        int i = a;
        System.out.println(i);
        float f1 = 30000000;
        System.out.println("jianxi:" + Math.ulp(f1));
        BigDecimal f1b = new BigDecimal(f1);
        float f2 = f1 + 1;

        BigDecimal f2b = new BigDecimal(f2);
        System.out.println(f1 == f2);

        System.out.println(f1b);
        System.out.println(f2b);

        List<String> list = new ArrayList<>();
        list.add("1");
        list.add("2");
        list.add("3");
        for (String i$ : list) {
            System.out.println(i$);
        }

        String s1 = "\17";
        String s2 = "\171";
        String s3 = "\1717";
        String s4 = "\43";
        String s5 = "\431";
        System.out.println(s1);
        System.out.println(s2);
        System.out.println(s3);
        System.out.println(s4);
        System.out.println(s5);

        int spi = 16;
        int spi2 = ++spi;
        System.out.println(spi);

        int sd = 16;
        sd = sd++;
        System.out.println(sd);

        short k = (short) -1;
        System.out.println(k >> 1);

        int[] str = {0, 0, 0, 0, 0};
        int index = 1;
        str[++index] = index++;
        System.out.println(Arrays.toString(str));
        test1(index, ++index, index = 2);
        test1(index = 5, index++, index);
        Main m = new Main();
        int test2 = 1;
        m.test2(test2, ++test2, test2);
        m.test2(test2 = 5, test2++, test2);

        Value v = new Value();
        v.x = 10;
        v.y = 20;
        m.swap4(v);
        System.out.println(v.x);
        System.out.println(v.y);


        Main main2 = new Main();
        Thread t2 = new Thread(() -> main2.threadMethod(1));
        Thread t1 = new Thread(new myCall(t2));
        Thread t3 = new Thread(new myCall(Thread.currentThread()));

        List<Integer> list1= new ArrayList<>();
        list1.add(2);
        list1.add(2);
        list1.add(2);
        list1.add(2);
        list1.add(2);
        list1.add(2);
        System.out.println("befor list size=" +list1.size());
        int size = list1.size();
        for (int j = 0; j < size; j++) {
            if(j%2==0){
                list1.add(1);
                list1.add(1);
            }
        }
        System.out.println("list size=" +list1.size());
    }


    //0x18283
    String str = new String("test");

    public void threadMethod(int i) {
        synchronized (str) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                throw new RuntimeException();
            }
            System.out.println(Thread.currentThread().getName());
        }
    }


    public static void test1(int a, int b, int c) {
        System.out.println(a);
        System.out.println(b);
        System.out.println(c);
    }

    public void test2(int a, int b, int c) {
        System.out.println(a);
        System.out.println(b);
        System.out.println(c);
    }

    /**
     * @param v
     */
    public void swap1(Value v) {
        int temp = v.x;
        v.x = v.y;
        v.y = temp;
    }

    /**
     * @param v
     */
    public void swap2(Value v) {
        v.x = v.x + v.y;
        v.y = v.x - v.y;
        v.x = v.x - v.y;
    }

    /**
     * v^y^y = x
     *
     * @param v
     */
    public void swap3(Value v) {
        v.x = v.x ^ v.y;
        v.y = v.x ^ v.y;
        v.x = v.x ^ v.y;
    }

    /**
     * @param v
     */
    public void swap4(Value v) {
        v.x = v.x - v.y;
        v.y = v.x + v.y;
        v.x = v.y - v.x;
    }


}

class Value {
    public int x;
    public int y;


}


class myCall implements Runnable {
    Thread t2;

    public myCall(Thread t1) {
        this.t2 = t1;
    }

    public Object call() throws Exception {
        t2.join();
        Main m = new Main();

        m.threadMethod(1);
        return "t1";
    }

    @Override
    public void run() {
        try {
            call();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
