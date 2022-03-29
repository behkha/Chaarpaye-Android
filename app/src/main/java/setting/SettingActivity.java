package setting;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.behkha.chaarpaye.R;

import utils.Fonts;
import utils.UserInfo;

/**
 * Created by Behzad Khanlar on 3/29/2018.
 */

public class SettingActivity extends AppCompatActivity {

    private Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_activity_layout);
        mContext = getApplicationContext();
        Typeface font = Fonts.get_iran_sans_font(mContext);

        ImageView mBack = (ImageView) findViewById(R.id.setting_toolbar_back);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        TextView mTitle = (TextView) findViewById(R.id.setting_toolbar_title);
        mTitle.setTypeface( font );

        Button mAboutChaarpaye = (Button) findViewById(R.id.setting_about_chaarpaye);
        mAboutChaarpaye.setTypeface(font);

        Button mLogout = (Button) findViewById(R.id.setting_logout);
        mLogout.setTypeface(font);

        Button mReport = (Button) findViewById(R.id.setting_report);
        mReport.setTypeface(font);

        Button mIntroduce = (Button) findViewById(R.id.setting_introduce);
        mIntroduce.setTypeface(font);
    }
    public void onAboutChaarpayeClick(View view){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("http://www.chaarpaye.ir"));
        startActivity(intent);
    }
    public void onLogoutClick(View view){
        UserInfo.removeToken(mContext);
        Intent i = getBaseContext().getPackageManager()
                .getLaunchIntentForPackage( getBaseContext().getPackageName() );
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }
    public void onReportClick(View view){
        String message = getResources().getString(R.string.report_crash_message);
        showMessageDialog(message);
    }
    public void onIntroduceClick(View view){
        String message = getResources().getString(R.string.intro_place_or_event_message);
        showMessageDialog(message);
    }
    private void showMessageDialog(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(mContext).inflate(R.layout.message_dialog_layout,null);
        builder.setView(dialogView);

        Typeface font = Fonts.get_iran_sans_font(mContext);
        TextView mMessage = dialogView.findViewById(R.id.message_dialog_message);
        TextView mOk = dialogView.findViewById(R.id.message_dialog_ok);
        mMessage.setTypeface(font);
        mOk.setTypeface(font);

        mMessage.setText(message);
        final AlertDialog dialog = builder.create();
        mOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dialog.isShowing())
                    dialog.dismiss();
            }
        });

        mMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/MashAdmin"));
                startActivity(intent);
            }
        });
        dialog.show();

    }
}
