package com.mridx.share.ui;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.mridx.share.R;
import com.mridx.share.adapter.FilesListAdapter;
import com.mridx.share.adapter.ViewPagerAdapter;
import com.mridx.share.callback.ReceiverCallback;
import com.mridx.share.callback.SenderCallback;
import com.mridx.share.data.FileData;
import com.mridx.share.data.FileSenderData;
import com.mridx.share.data.Utils;
import com.mridx.share.fragment.AppFragmentNew;
import com.mridx.share.fragment.MusicFragment;
import com.mridx.share.fragment.PhotoFragment;
import com.mridx.share.fragment.VideoFragment;
import com.mridx.share.thread.FileReceiver;
import com.mridx.share.thread.FileSender;
import com.mridx.share.utils.FileSenderType;

import java.io.IOException;
import java.util.ArrayList;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;

public class MainUI extends AppCompatActivity implements FilesListAdapter.OnAdapterItemClicked, Function2<ArrayList<Object>, FileSenderType, Unit>, SenderCallback, ReceiverCallback {

    public ViewPager viewPager;
    private TabLayout tabLayout;
    private ViewPagerAdapter viewPagerAdapter;


    OnItemClickedListener onItemClickedListener;


    public interface OnItemClickedListener {
        void onClicked(FileData fileData);
    }

    public void setOnItemClickedListener(OnItemClickedListener onItemClickedListener) {
        this.onItemClickedListener = onItemClickedListener;
    }

    OnBackPressed onBackPressed;

    public interface OnBackPressed {
        void onPressed();
    }

    public void setOnBackPressed(OnBackPressed onBackPressed) {
        this.onBackPressed = onBackPressed;
    }

    public Utils.TYPE userType;
    public String ip = "";
    private FileReceiver fileReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_ui);

        if (getIntent().getExtras() == null) {
            finish();
            return;
        }
        userType = (Utils.TYPE) getIntent().getExtras().get("TYPE");
        if (userType == Utils.TYPE.CLIENT) {
            fileReceiver = new FileReceiver(Utils.TYPE.CLIENT);
            fileReceiver.start();
            fileReceiver.setReceiverCallback(this);
        }
        if (userType == Utils.TYPE.HOST) {
            fileReceiver = new FileReceiver(Utils.TYPE.HOST);
            fileReceiver.start();
            fileReceiver.setReceiverCallback(this);
            //getIP();
        }

        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager.setOffscreenPageLimit(5);

        tabLayout.setupWithViewPager(viewPager);
        AppFragmentNew appFragmentNew = new AppFragmentNew();
        appFragmentNew.setOnSendAction(this);
        MusicFragment musicFragment = new MusicFragment();
        musicFragment.setOnSendAction(this);
        VideoFragment videoFragment = new VideoFragment();
        videoFragment.setOnSendAction(this);
        /*FileFragment fileFragment = new FileFragment();
        fileFragment.setOnFilesSendAction(this::onFileSendAction);*/

        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(appFragmentNew, "APP");
        viewPagerAdapter.addFragment(new PhotoFragment(), "PHOTO");
        viewPagerAdapter.addFragment(musicFragment, "MUSIC");
        viewPagerAdapter.addFragment(videoFragment, "VIDEO");
        //viewPagerAdapter.addFragment(new FileFragment(), "FILE");


        viewPager.setAdapter(viewPagerAdapter);

    }


    @Override
    public void onClicked(FileData fileData) {
        onItemClickedListener.onClicked(fileData);
    }


    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 4)
            onBackPressed.onPressed();
        else
            super.onBackPressed();
    }

    @Override
    public Unit invoke(ArrayList<Object> objects, FileSenderType fileSenderType) {
        FileSender fileSender = new FileSender(new FileSenderData(objects, fileSenderType, userType, userType == Utils.TYPE.CLIENT ? Utils.HOST_IP : Utils.CLIENT_IP));
        fileSender.setFileSenderCallback(this);
        fileSender.start();
        return null;
    }

    private void onFileSendAction(ArrayList<Object> objects, FileSenderType type) {
        FileSender fileSender = new FileSender(new FileSenderData(objects, type, userType, ip));
        fileSender.setFileSenderCallback(this);
        fileSender.start();
    }

    @Override
    public void setOnSenderCallback(boolean connected, IOException error) {
        runOnUiThread(() -> {
            if (connected) {
                Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Something went wrong ! " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void setOnFileTransferCallback() {
        runOnUiThread(()-> {
            Toast.makeText(this, "File send complete", Toast.LENGTH_SHORT).show();
        });

    }

    @Override
    public void onFileSendingStart() {
        // TODO: 31/08/20 file sending started
    }

    @Override
    public void onFileSendingProgress(String p1) {
        runOnUiThread(() -> {
            Toast.makeText(this, "File send " + p1, Toast.LENGTH_SHORT).show();
        });

    }

    @Override
    public void onConnected(boolean connected, IOException error) {
        runOnUiThread(() ->
                Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show());

    }

    @Override
    public void onComplete() {
        runOnUiThread(() ->
                Toast.makeText(this, "Transfer complete", Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onStartReceiving() {
        runOnUiThread(() ->
                Toast.makeText(this, "Start receiving", Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onReceivingProgress(String p1) {
        runOnUiThread(() -> Toast.makeText(this, "receiving file - " + p1, Toast.LENGTH_SHORT).show());
    }
}