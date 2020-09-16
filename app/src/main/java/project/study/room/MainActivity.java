package project.study.room;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

import io.reactivex.disposables.Disposable;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import project.study.room.Adapter.ExampleAdapter;
import project.study.room.Adapter.ExampleItem;
import project.study.room.Retrofit.JsonPlaceHolderApi;
import project.study.room.Room.AppDatabase;
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
    MethodForDb methods;

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

        methods = new MethodForDb();

    }

    Disposable disposable;
    public void ActionsDB(View view) {
        switch (view.getId()){
            case R.id.InsertAll:
                methods.InsertAllData(getApplicationContext(),db,jsonPlaceHolderApi);
                break;
            case R.id.Insert:
                AlertDialogInsert();
                break;
            case R.id.GetDate:
                methods.GetAllData(disposable,db,mExampleList,mAdapter);
                break;
            case R.id.Delete:
                DeleteUser();
                break;
            case R.id.Update:
                UpdateUser();
                break;
            case R.id.DeleteAll:
                methods.DeleteAllDate(mExampleList,mAdapter,db,getApplicationContext());
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

                methods.update(db,customLayout,getApplicationContext());
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

                methods.delete(customLayout,db,getApplicationContext());
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

                methods.insert(customLayout,db, getApplicationContext());

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }
}