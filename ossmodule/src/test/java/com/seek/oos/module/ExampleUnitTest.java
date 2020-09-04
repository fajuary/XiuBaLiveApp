package com.seek.oos.module;

import com.xiu8.base.util.MD5Utils;

import org.junit.Test;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.UUID;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);

//        Observable.just(1, 2, 3).map(new Function<Integer, Integer>() {
//            @Override
//            public Integer apply(Integer integer) throws Exception {
//                if (integer == 2) throw new Exception("Error");
//                return integer;
//            }
//        }).lastElement().flatMapSingle(new Function<Integer, SingleSource<Integer>>() {
//            @Override
//            public SingleSource<Integer> apply(Integer integer) throws Exception {
//                return Single.just(integer);
//            }
//        }).subscribe(new Consumer<Integer>() {
//            @Override
//            public void accept(Integer integer) throws Exception {
//                System.out.println("Integer:" + integer);
//            }
//        }, new Consumer<Throwable>() {
//            @Override
//            public void accept(Throwable throwable) throws Exception {
//                System.out.println("Integer:" + throwable.getMessage());
//            }
//        });

        String path = "/Users/chunyang/test.mp3";

        System.out.println(MD5Utils.stringMd5(UUID.randomUUID().toString()));
        System.out.print((path).substring(path.lastIndexOf("."), path.length()));

        Thread.sleep(4000);
    }


}