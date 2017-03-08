package com.devtau.rx;

import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import com.jakewharton.rxbinding.widget.RxSearchView;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

	private static final String LOG_TAG = "MainActivityLog";
	private static final int DEBOUNCE_RATE = 200;
	private Observable<String> searchViewObservable;
	private Subscriber<String> myFullSubscriber;
	private ListAdapter mAdapter;


	@Bind(R.id.recycler_view)
	protected RecyclerView mRecyclerView;

	@OnClick(R.id.subscribe_button)
	public void subscribe() {
		//после выполнения команды unsubscribe() подписчик более не может использоваться и его нужно переинициализировать
		myFullSubscriber = initSubscriber();
		searchViewObservable.subscribe(myFullSubscriber);
	}

	@OnClick(R.id.unsubscribe_button)
	public void unsubscribe() {
		if (myFullSubscriber != null) {
			myFullSubscriber.unsubscribe();
		}
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);

		List<String> list = new ArrayList<>();
		list.add("first");
		list.add("second");
		list.add("third");
		mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
		mRecyclerView.setHasFixedSize(true);
		mAdapter = new ListAdapter(list);
		mRecyclerView.setAdapter(mAdapter);
		Log.d(LOG_TAG, String.valueOf(mRecyclerView.getAdapter().getItemCount()));
	}


	private Subscriber<String> initSubscriber() {
		//развернутый вариант Subscriber
		return new Subscriber<String>() {
			@Override
			public void onNext(String s) {
				print("myFullSubscriber onNext " + s);
			}

			@Override
			public void onCompleted() {
				print("myFullSubscriber onCompleted");
			}

			@Override
			public void onError(Throwable e) {
				print("myFullSubscriber onError " + e.getMessage());
			}
		};
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);

		MenuItem searchItem = menu.findItem(R.id.action_search);
		SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
		searchViewObservable = RxSearchView.queryTextChanges(searchView)
				.debounce(DEBOUNCE_RATE, TimeUnit.MILLISECONDS)
				.observeOn(AndroidSchedulers.mainThread())
				.map(CharSequence::toString);
//				.map(new Func1<CharSequence, String>() {
//					@Override
//					public String call(CharSequence charSequence) {
//						return charSequence.toString();
//					}
//				});
		return true;
	}


	private void print(final String msg) {
		Log.d(LOG_TAG, msg);
		List<String> list = new ArrayList<>();
		list.add("first");
		list.add("second");
		mAdapter = new ListAdapter(list);
		//сейчас мы на фоновом потоке и UI поток недоступен без Handler
		mRecyclerView.setAdapter(mAdapter);
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onStop() {
		super.onStop();
		unsubscribe();
	}




	//развернутый вариант Observable
	Observable<String> myFullObservable = Observable.create(
			new Observable.OnSubscribe<String>() {
				@Override
				public void call(Subscriber<? super String> subscriber) {
					subscriber.onNext("myFullObservable");
					//или onError или onCompleted. каждый из них прерывает дальнейшее выполнение метода
					subscriber.onCompleted();
					subscriber.onError(new NullPointerException("NPE"));
				}
			}
	);

	//урезанный подвид Observable
	//выполнит onNext с переданным параметром и onCompleted
	Observable<String> myShortObservable = Observable.just("myShortObservable")
			.map(new Func1<String, String>() {
				@Override
				public String call(String s) {
					return s + " -Dan";
				}
			});

	//урезанный подвид Subscriber
	Action1<String> myShortSubscriber = new Action1<String>() {
		@Override
		public void call(String s) {
			print("myShortSubscriber " + s);
		}
	};
}
