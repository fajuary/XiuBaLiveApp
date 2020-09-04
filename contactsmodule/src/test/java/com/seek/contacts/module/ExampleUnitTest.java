package com.seek.contacts.module;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

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
//        System.out.println(Thread.currentThread().getName() + " addition_isCorrect: ");

        List<String> list = new ArrayList<>();
        list.add("1");
        list.add("1");
        list.add("1");
        list.add("1");
        List<String> data = new ArrayList<>();
        data.addAll(list);
        data.addAll(list);
        data.addAll(list);
        data.addAll(list);
        data.addAll(list);

        List<String> newList = new ArrayList<>(data);
        newList.addAll(data);
        newList.addAll(data);
        newList.addAll(data);
        newList.addAll(data);
        System.out.println("data" + newList.size());

        System.out.println(newList.size());

        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                int count = 0;
                while (count <= 0) {
                    emitter.onNext(String.valueOf(count));
                    count++;
                }
                emitter.onComplete();
            }
        }).doOnComplete(new Action() {
            @Override
            public void run() throws Exception {
                System.out.println("create doOnComplete");
            }
        }).flatMap(new Function<String, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(String s) throws Exception {
                if (s.equals("1")) {
                    return Observable.error(new Throwable("Error"));
                }
                return Observable.just(s + "change").doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        System.out.println("change doOnComplete");
                    }
                });
            }
        }).doOnError(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                System.out.println("new  doOnError");
            }
        }).doOnComplete(new Action() {
            @Override
            public void run() throws Exception {
                System.out.println("new  doOnComplete");
            }
        }).subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io()).observeOn(Schedulers.newThread()).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String s) {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });

        Thread.sleep(2000);

    }
}