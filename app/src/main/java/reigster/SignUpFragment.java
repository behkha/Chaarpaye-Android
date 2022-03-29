package reigster;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.behkha.chaarpaye.MainActivity;
import com.behkha.chaarpaye.R;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.SignUpEvent;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

import utils.Fonts;
import utils.Server;
import utils.UserInfo;

/**
 * Created by User on 3/18/2018.
 */

public class SignUpFragment extends Fragment {

    private Context mContext;
    private final String TAG = "SIGN_UP_REQUEST_QUEUE_TAG";
    private final int ERROR_CODE = 422;
    private RequestQueue mRequestQueue;

    private MaterialEditText mName;
    private MaterialEditText mPoe;
    private MaterialEditText mPassword;
    private AVLoadingIndicatorView mProgressBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sign_up_fragment_layout , container , false);
        this.mContext = view.getContext();
        Typeface font = Fonts.get_iran_sans_font(mContext);

        mProgressBar = view.findViewById(R.id.sign_up_progress_bar);

        TextView title = view.findViewById(R.id.sign_up_title);
        title.setTypeface(Fonts.get_iran_sans_bold_font(mContext));

        mName = view.findViewById(R.id.sign_up_name);
        mPoe = view.findViewById(R.id.sign_up_poe);
        mPassword = view.findViewById(R.id.sign_up_password);
        TextView mSignUp = view.findViewById(R.id.sign_up_sign_up);
        TextView mAlreadySignUp = view.findViewById(R.id.sign_up_already_signed_up);

        mName.setTypeface(font);
        mPoe.setTypeface(font);
        mPassword.setTypeface(font);
        mSignUp.setTypeface(font);
        mAlreadySignUp.setTypeface(font);

        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSignUpClick();
            }
        });
        mAlreadySignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAlreadySignedUpClick();
            }
        });
        return view;
    }

    public void onSignUpClick(){
        String nameInput = mName.getText().toString().trim();
        String poeInput = mPoe.getText().toString().trim();
        String passwordInput = mPassword.getText().toString().trim();

        if (nameInput.isEmpty()){
            mName.setError("نام نمی تواند خالی باشد");
            return;
        }
        if (poeInput.isEmpty()){
            mPoe.setError("شماره همراه یا ایمیل نمی تواند خالی باشد");
            return;
        }
        if (passwordInput.isEmpty()){
            mPassword.setError("رمز عبور نمی تواند خالی باشد");
            return;
        }
        if (passwordInput.length() < 6 || passwordInput.length() > 32){
            mPassword.setError("رمز عبور باید بین 6 و 32 کاراکتر باشد");
            return;
        }

        nameInput = Server.convertPersianToLatin(nameInput);
        poeInput = Server.convertPersianToLatin(poeInput);
        passwordInput = Server.convertPersianToLatin(passwordInput);

        if (isEmail(poeInput) || isPhoneNumber(poeInput)) {
            if (isEmail(poeInput)) storeData("email", poeInput);
            if (isPhoneNumber(poeInput)) storeData("tell", poeInput);
            register(nameInput , poeInput, passwordInput);
        } else
            Toast.makeText(mContext, "شماره همراه یا ایمیل وارد شده معتبر نمی باشد", Toast.LENGTH_SHORT).show();
    }

    public void onAlreadySignedUpClick(){
        ViewPager viewPager = getActivity().findViewById(R.id.register_view_pager);
        viewPager.setCurrentItem(0,false);
    }

    private void register(final String name, final String poe, String password){
        mProgressBar.setVisibility(View.VISIBLE);
        String url = Server.DOMAIN + Server.API + "/users";
        JSONObject postParams = new JSONObject();
        final String inputType = getInputType();
        if (inputType == null)
            return;
        try {
            postParams.put("name",name);
            postParams.put(inputType,poe);
            postParams.put("password",password);
        } catch (JSONException ex){
            ex.printStackTrace();
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, postParams,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String token = response.getString("access_token");
                            UserInfo.setToken(mContext, token);
                            Toast.makeText(mContext, "با موفقیت وارد حساب کاربری خود شدید", Toast.LENGTH_SHORT).show();
                            Answers.getInstance().logSignUp(new SignUpEvent()
                                .putMethod(name + " " + poe).putSuccess(true)
                            );
                            Intent intent = new Intent(mContext, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            ((Activity) mContext).finish();
                            mProgressBar.setVisibility(View.GONE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            mProgressBar.setVisibility(View.GONE);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mProgressBar.setVisibility(View.GONE);
                        if (error == null){
                            alreaySignedUp(inputType);
                            return;
                        }
                        if (error instanceof ServerError){
                            alreaySignedUp(inputType);
                            return;
                        }
                        Server.errorHandler(mContext, error);
                    }
                });
        addToRequestQueue(request,TAG);
    }

    private String getInputType() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("REGISTER_DATA", Context.MODE_PRIVATE);
        return sharedPreferences.getString("INPUT_TYPE", null);
    }

    private String getInput() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("REGISTER_DATA", Context.MODE_PRIVATE);
        return sharedPreferences.getString("INPUT", null);
    }

    private void storeData(String inputType, String input) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("REGISTER_DATA", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("INPUT_TYPE", inputType);
        editor.putString("INPUT", input);
        editor.apply();
    }

    private boolean isPhoneNumber(String text) {
        return Patterns.PHONE.matcher(text).matches();
    }

    private boolean isEmail(String text) {
        return !TextUtils.isEmpty(text) && Patterns.EMAIL_ADDRESS.matcher(text).matches();
    }

    private void alreaySignedUp(String inputType){
        String input;
        if (inputType.equals("tell"))
            input = "شماره همراه";
        else
            input = "ایمیل";
        String errorText = input + " یا نام وارد شده قبلا استفاده شده است ";
        Toast.makeText(mContext, errorText, Toast.LENGTH_LONG).show();
    }

    private RequestQueue getRequestQueue() {
        if (mRequestQueue == null)
            mRequestQueue = Volley.newRequestQueue(mContext);
        return mRequestQueue;
    }
    private void addToRequestQueue(Request request, String tag) {
        request.addMarker(tag);
        getRequestQueue().add(request);
    }
    private void cancelAllRequests(String tag) {
        getRequestQueue().cancelAll(tag);
    }

    @Override
    public void onStop() {
        super.onStop();
        cancelAllRequests(TAG);
    }
}
