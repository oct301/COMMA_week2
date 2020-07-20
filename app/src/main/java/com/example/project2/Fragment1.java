package com.example.project2;


import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.project2.Retrofit.IMyService;
import com.example.project2.Retrofit.RetrofitClient;

import org.json.JSONArray;
import org.w3c.dom.Text;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class Fragment1 extends Fragment{

    private RecyclerView recyclerView;
    private ListView lv;
    String user_email;


    public Fragment1() {
        // Required empty public constructor

    }

    public static Fragment1 newinstance(String email) {
        Fragment1 fragment = new Fragment1();
        Bundle args = new Bundle();
        args.putString("email", email);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user_email = getArguments().getString("email");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment1, container, false);
        ArrayList<Person> phone_address= new ArrayList<>();
        ContactAdapter contactAdapter = new ContactAdapter(getContext(), R.layout.contact_layout, phone_address);
        lv = (ListView) v.findViewById(R.id.list);
        lv.setAdapter(contactAdapter);


        Log.i("여기", "입장");
        Retrofit retrofitClient = RetrofitClient.getInstance();
        IMyService iMyService = retrofitClient.create(IMyService.class);
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(iMyService.getContact(user_email)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String response) throws Exception {
                        JSONArray jsonArray = new JSONArray(response);
                        for (int i=0; i<jsonArray.length(); i++) {
                            String name = jsonArray.getJSONObject(i).getString("name");
                            String email = jsonArray.getJSONObject(i).getString("email");
                            String phone_number = jsonArray.getJSONObject(i).getString("phone_number");
                            Log.i("유저 정보", name + " / " + email + " / " + phone_number);
                            contactAdapter.getItems(new Person(name, email, phone_number, new Long(0)));
                        }
                        contactAdapter.notifyDataSetChanged();
                    }
                }));
        Log.i("End", "Game");

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowID)
            {
                doSelectFriend((Person)parent.getItemAtPosition(position));
            }});

        ImageButton newContact = (ImageButton) v.findViewById(R.id.newContact);
        newContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newPostIntent = new Intent(v.getContext(), NewContact.class);
                newPostIntent.putExtra("user_email", user_email);
                startActivity(newPostIntent);

            }
        });

        ImageButton delete_Contact = (ImageButton) v.findViewById(R.id.delete_Contact);
        delete_Contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newPostIntent = new Intent(v.getContext(), delete_Contact.class);
                newPostIntent.putExtra("user_email", user_email);
                startActivity(newPostIntent);

            }
        });
        ImageView imageView;
        Drawable a;
        ImageButton refresh = (ImageButton) v.findViewById(R.id.refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CompositeDisposable compositeDisposable = new CompositeDisposable();
                compositeDisposable.add(iMyService.getContact(user_email)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<String>() {
                            @Override
                            public void accept(String response) throws Exception {
                                contactAdapter.clear();
                                JSONArray jsonArray = new JSONArray(response);
                                for (int i=0; i<jsonArray.length(); i++) {
                                    String name = jsonArray.getJSONObject(i).getString("name");
                                    String email = jsonArray.getJSONObject(i).getString("email");
                                    String phone_number = jsonArray.getJSONObject(i).getString("phone_number");
                                    Log.i("유저 정보", name + " / " + email + " / " + phone_number);
                                    contactAdapter.getItems(new Person(name, email, phone_number, new Long(0)));
                                }
                                contactAdapter.notifyDataSetChanged();
                            }
                        }));
            }
        });
        return v;
    }

    public void doSelectFriend(Person p) {
        Log.d("Clicked", p.getName() + ", " + p.getNumber() + ", " + p.getEmail());
        //Intent intent = new Intent(getContext(), PopupActivity.class);
        //intent.putExtra("user", p.getName());
        //intent.putExtra("data", "Test Popup");
        //startActivityForResult(intent, 1);

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
        View mView = getLayoutInflater().inflate(R.layout.popup_user, null);
        Button send = (Button) mView.findViewById(R.id.send);
        TextView txtText = (TextView)mView.findViewById(R.id.txtText);
        TextView user = (TextView)mView.findViewById(R.id.userTo);
        //txtText.setText("익명으로 전달됩니다");
        user.setText("To. "+p.getName());

        for(int i=1; i<=3; i++){

        }
        ImageView img1 = mView.findViewById(R.id.profile1);
        ImageView img2 = mView.findViewById(R.id.profile2);
        ImageView img3 = mView.findViewById(R.id.profile3);
        Glide.with(getContext())
                //.load(imageUrls.get(i).getImageUrl()) // 웹 이미지 로드
                .load("qweqweqwe") // 이미지 로드
                .error(R.drawable.noimage)
                .override(500,500) //해상도 최적화
                .thumbnail(0.3f) //섬네일 최적화. 지정한 %만큼 미리 이미지를 가져와 보여주기
                .centerCrop() // 중앙 크롭
                .into(img1);

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("clicked","button");
                // Send 버튼 눌렀을 때 서버로 저장하는 코드 작성 필요



                // ====================
                Toast.makeText(getContext(), "익명으로 "+ p.getName()+"님께 메세지가 전송되었습니다", Toast.LENGTH_SHORT).show();
                dialog.dismiss(); // 팝업 종료
            }
        });
    }
}