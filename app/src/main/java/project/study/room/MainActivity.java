package project.study.room;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableSingleObserver;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import project.study.room.Adapter.ExampleAdapter;
import project.study.room.Adapter.ExampleItem;
import project.study.room.Retrofit.JsonPlaceHolderApi;
import project.study.room.Retrofit.Post;
import project.study.room.Room.AppDatabase;
import project.study.room.Room.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {


    private JsonPlaceHolderApi jsonPlaceHolderApi;

    RecyclerView recyclerView;

    private ArrayList<ExampleItem> mExampleList;
    private ExampleAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    private RetrofitMethods retrofitMethods;
    AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        mExampleList = new ArrayList<>();

        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new ExampleAdapter(mExampleList);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mAdapter);

        db = Room.databaseBuilder(this.getApplicationContext(),
                AppDatabase.class, "User")
                .allowMainThreadQueries()
                .build();

        RxJavaCallAdapterFactory rxAdapter = RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io());

        Gson gson = new GsonBuilder().serializeNulls().create();
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();

         // connect retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://jsonplaceholder.typicode.com/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(rxAdapter)
                .client(okHttpClient)
                .build();


        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

        retrofitMethods = new RetrofitMethods(jsonPlaceHolderApi,getApplicationContext());

    }

    Disposable disposable;
    public void ActionsDB(View view) {
        switch (view.getId()){
            case R.id.InsertAll:
                InsertAllData();
                break;
            case R.id.Insert:
                AlertDialogInsert();
                break;
            case R.id.GetDate:
                GetAllData();
                break;
            case R.id.Delete:
                DeleteUser();
                break;
            case R.id.Update:
                UpdateUser();
                break;
            case R.id.DeleteAll:
               DeleteAllDate();
                break;
        }
    }


    private void UpdateUser() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update");

        final View customLayout = getLayoutInflater().inflate(R.layout.custom_dialog, null);
        builder.setView(customLayout);
        // add a button

        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                EditText id = customLayout.findViewById(R.id.id);
                final EditText userId = customLayout.findViewById(R.id.userId);
                final EditText title = customLayout.findViewById(R.id.title);
                final EditText text = customLayout.findViewById(R.id.text);

                if(id != null) {

                    db.userDAO().findUserById(Integer.valueOf(id.getText().toString()))
                            .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new DisposableSingleObserver<User>() {
                                @Override
                                public void onSuccess(User user) {

                                    if (userId != null) {
                                        user.setUserId(Integer.valueOf(userId.getText().toString()));
                                    }
                                    if (title != null) {
                                        user.setTitle(title.getText().toString());
                                    }
                                    if (text != null) {
                                        user.setText(text.getText().toString());
                                    }

                                    db.userDAO().updateUser(user)
                                            .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(new Action() {
                                                @Override
                                                public void run() throws Exception {
                                                    Toast.makeText(MainActivity.this, "Delete", Toast.LENGTH_SHORT).show();
                                                }
                                            }, new Consumer < Throwable > () {
                                                @Override
                                                public void accept(Throwable throwable) throws Exception {
                                                    Toast.makeText(MainActivity.this, "" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                    Toast.makeText(MainActivity.this, "Date get", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onError(Throwable e) {
                                    Toast.makeText(MainActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void DeleteUser() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete");

        final View customLayout = getLayoutInflater().inflate(R.layout.custom_dialog, null);
        builder.setView(customLayout);
        // add a button

        final EditText userId = customLayout.findViewById(R.id.userId);
        final EditText title = customLayout.findViewById(R.id.title);
        final EditText text = customLayout.findViewById(R.id.text);

        userId.setVisibility(View.GONE);
        title.setVisibility(View.GONE);
        text.setVisibility(View.GONE);

        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                EditText id = customLayout.findViewById(R.id.id);

                if(id != null) {

                    db.userDAO().findUserById(Integer.valueOf(id.getText().toString()))
                            .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new DisposableSingleObserver<User>() {
                                @Override
                                public void onSuccess(User user) {

                                    db.userDAO().deleteUser(user)
                                            .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(new Action() {
                                                @Override
                                                public void run() throws Exception {
                                                    Toast.makeText(MainActivity.this, "Delete", Toast.LENGTH_SHORT).show();
                                                }
                                            }, new Consumer<Throwable>() {
                                                @Override
                                                public void accept(Throwable throwable) throws Exception {
                                                    Toast.makeText(MainActivity.this, "" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                    Toast.makeText(MainActivity.this, "Date get", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onError(Throwable e) {
                                    Toast.makeText(MainActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void GetAllData() {
        disposable =  db.userDAO().getAllUsers()
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<User>>() {
                    @Override
                    public void accept(List<User> users) throws Exception {
                        mExampleList.clear();
                        if(users.size() != 0) {
                            for (int i = 0; i < users.size(); i++) {
                                mExampleList.add(new ExampleItem(users.get(i).getId(),
                                        users.get(i).getUserId(),
                                        users.get(i).getTitle(),
                                        users.get(i).getText()));
                            }
                            mAdapter.notifyDataSetChanged();
                        }

                    }
                },new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        System.out.println("xxx error "+throwable);
                    }
                });
    }

    private void InsertAllData(){
        Call<List<Post>> call = jsonPlaceHolderApi.getPosts();
        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Code: " + response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }
                List<Post> posts = response.body();
                for (Post post : posts) {
                    int userId = post.getUserId();
                    String title = post.getTitle();
                    String text = post.getText();

                    db.userDAO().insertUser(new User(userId,
                            title,
                            text))
                            .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Action() {
                                @Override
                                public void run() throws Exception {
                                    //Toast.makeText(MainActivity.this, "Insert", Toast.LENGTH_SHORT).show();
                                }
                            }, new Consumer < Throwable > () {
                                @Override
                                public void accept(Throwable throwable) throws Exception {
                                    //Toast.makeText(MainActivity.this, "" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void AlertDialogInsert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Insert");

        final View customLayout = getLayoutInflater().inflate(R.layout.custom_dialog, null);
        builder.setView(customLayout);
        // add a button

        EditText id = customLayout.findViewById(R.id.id);
        id.setVisibility(View.GONE);

        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                EditText userId = customLayout.findViewById(R.id.userId);
                EditText title = customLayout.findViewById(R.id.title);
                EditText text = customLayout.findViewById(R.id.text);

                db.userDAO().insertUser(new User(Integer.valueOf(userId.getText().toString()),
                        title.getText().toString(),text.getText().toString())).
                        subscribeOn(io.reactivex.schedulers.Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action() {
                            @Override
                            public void run() throws Exception {
                                Toast.makeText(MainActivity.this, "Insert", Toast.LENGTH_SHORT).show();
                            }
                        }, new Consumer < Throwable > () {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                Toast.makeText(MainActivity.this, "" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void DeleteAllDate(){
        mExampleList.clear();
        mAdapter.notifyDataSetChanged();
        db.userDAO().deleteAll()
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action() {
                    @Override
                    public void run() throws Exception {
                        Toast.makeText(MainActivity.this, "Delete", Toast.LENGTH_SHORT).show();
                    }
                }, new Consumer < Throwable > () {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(MainActivity.this, "" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }
}