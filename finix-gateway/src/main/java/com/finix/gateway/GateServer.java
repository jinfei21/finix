package com.finix.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;

import rx.Observable;
import rx.Producer;
import rx.Subscriber;


@SpringBootApplication
@Configuration
public class GateServer {

	public static void main(String[] args) {
        SpringApplication.run(GateServer.class, args);

//
//		Observable<String> ob = Observable.just("t", "a");
//
//		ob.subscribe(new Subscriber() {
//
//			@Override
//			public void onCompleted() {
//				System.out.println("completed");
//
//			}
//
//			@Override
//			public void onError(Throwable arg0) {
//				System.out.println("error" + arg0.getMessage());
//
//			}
//
//			@Override
//			public void onNext(Object arg0) {
//
//				System.out.println("next" + arg0);
//
//				throw new RuntimeException("jinfei");
//			}
//
//		});
//
//		Observable<Integer> observable1 = Observable.just(1, 2, 3,4);
//		Observable<Integer> observable2 = Observable.just(11, 22, 33);
//
//		Observable.zip(observable1, observable2, new Func2<Integer, Integer, Integer>() {
//			@Override
//			public Integer call(Integer integer, Integer integer2) {
//				return integer * integer2;
//			}
//		}).subscribe(new Action1<Integer>() {
//			@Override
//			public void call(Integer integer) {
//				System.out.println("i = " + integer);
//			}
//		});
//
//		
//		Observable.just("Hello, world!")
//	    .subscribe(s -> System.out.println(s));
//		
		
		
		
//		JProducer producer = new JProducer();
//		
//		Observable<String> observable = Observable.create(subscriber -> {
//			
//			subscriber.setProducer(producer);
//			producer.subscriptionStart(subscriber);
//			System.out.println("fsdfa");
//		});
//		
//		observable.doOnTerminate(()->{
//			System.out.println("doOnTerminate");
//		});
//		
//		observable.doOnNext( key ->{
//			System.out.println(key);
//		});
//		
//		observable.subscribe(new Observer<String>() {
//            @Override
//            public void onCompleted() {
//            	System.out.println("onCompleted");
//            }
//
//            @Override
//            public void onError(Throwable e) {
//            	System.out.println("onError");
//            }
//
//            @Override
//            public void onNext(String s) {
//            	System.out.println("onNext");
//            }
//        });
//		
//
//	}
//
//	
//	public static class JProducer implements Producer {
//		Subscriber<? super String> contentSubscriber;
//		@Override
//		public void request(long n) {
//			System.out.println("request");
//		}
//		
//        void subscriptionStart(Subscriber<? super String> subscriber) {
//            this.contentSubscriber = subscriber;
//            notifySubscriber();
//        }
//        
//        void notifySubscriber() {
//        	this.contentSubscriber.onNext("JProducer");
//        	this.contentSubscriber.onCompleted();
//        }
		
		TestSubscriber testSubscriber = new TestSubscriber();
		
		Observable<String> observable1 = Observable.create(subscriber -> {
			
			System.out.println("fsdfa");
			
			subscriber.setProducer( new Producer() {
				
				@Override
				public void request(long n) {
					System.out.println("request");
					
				}
			});
		});
		
		
		Observable<String> observable = Observable.create(subscriber -> {
			
			new WriteRequest(testSubscriber,observable1).write();
		
			subscriber.onNext("jinfei");
			subscriber.onCompleted();
		});
		
		
		observable.doOnTerminate(()->{
			System.out.println("test");
		}).map(r -> {
			return r;
		});
		observable.subscribe(new Subscriber<String>(){

			@Override
			public void onCompleted() {
				System.out.println("c");
			}

			@Override
			public void onError(Throwable e) {
				
			}

			@Override
			public void onNext(String t) {
				System.out.println(t);
			}
			
		});

		System.out.println("fsaf");
		
		
	}
	
	static class TestSubscriber extends Subscriber<String>{
			
		public TestSubscriber(){
				
		}
			
		@Override
		public void onCompleted() {
				
		}
			
		@Override
		public void onError(Throwable e) {
				
		}
			
		@Override
		public void onNext(String t) {
			System.out.println("my:"+t);
			request(1);
		}
			
		
	}
	
	 static class WriteRequest {
		private Subscriber< String> subscriber;
		private Observable<String> observable;
		
		public WriteRequest(Subscriber<String> subscriber,Observable<String> observable){
			this.subscriber = subscriber;
			this.observable = observable;
		}
		
		public void write(){
			observable.subscribe(subscriber);
		}
	};
}
