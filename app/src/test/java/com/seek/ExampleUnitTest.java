package com.seek;

import org.junit.Test;

import java.text.DecimalFormat;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {


    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);

//        DecimalFormat df1 = new DecimalFormat("0.00");
//        bigDecimal.setScale(2);
//        System.out.println(df1.format(3.122121));


        System.out.println("1ede47f1aa2b08808aaeaf4f9c55e1ab".substring(2, 17).toLowerCase());
//        Observable.just(1).flatMap(new Function<Integer, ObservableSource<Integer>>() {
//            @Override
//            public ObservableSource<Integer> apply(Integer integer) throws Exception {
//                return Observable.just(2);
//            }
//        }).subscribe(new Consumer<Integer>() {
//            @Override
//            public void accept(Integer integer) throws Exception {
//                System.out.println("integer:" + integer);
//            }
//        });
//
//        Thread.sleep(2000);
    }

}