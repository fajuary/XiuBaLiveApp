package com.seek.seekannmodule;

import com.seek.ann.AnnRetryFunc;

import org.junit.Test;
import org.reactivestreams.Publisher;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

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

        List<String> list = new ArrayList<>();
        list.add("1");
        list.add("2");
        list.add("3");
        list.add("4");

        Observable.fromIterable(list).flatMap(new Function<String, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(String s) throws Exception {

                return annService(s).doOnError(new Consumer<Throwable>() {
                    int count = 0;

                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        count++;
//                        System.out.println("doOnError " + count);
                    }
                }).retryWhen(new Function<Observable<Throwable>, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(Observable<Throwable> throwableObservable) throws Exception {
                        return throwableObservable.flatMap(new Function<Throwable, ObservableSource<?>>() {
                            int count = 0;

                            @Override
                            public ObservableSource<?> apply(Throwable throwable) throws Exception {
                                if (count < 2) {
                                    count++;
//                                    System.out.println("retryWhen " + count);
                                    return Observable.timer(1000, TimeUnit.MILLISECONDS);
                                }
                                return Observable.error(throwable);
                            }
                        });
                    }
                }).onErrorReturn(new Function<Throwable, String>() {
                    @Override
                    public String apply(Throwable throwable) throws Exception {
                        return "error";
                    }
                });
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                System.out.println("subscribe Success " + s);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                System.out.println("subscribe Error");
            }
        });


        Thread.sleep(14000);

    }

    public Observable<String> annService(final String string) {
        System.out.println("annService " + string);
        if (string.equals("2")) {

            return Observable.timer(1000, TimeUnit.MILLISECONDS).flatMap(new Function<Long, ObservableSource<String>>() {
                @Override
                public ObservableSource<String> apply(Long aLong) throws Exception {
                    return Observable.just(string);
                }
            });
        } else {
            return Observable.error(new Throwable("OnError"));
        }
    }


}