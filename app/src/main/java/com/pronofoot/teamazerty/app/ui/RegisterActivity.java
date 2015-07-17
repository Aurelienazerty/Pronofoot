package com.pronofoot.teamazerty.app.ui;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.kevinsawicki.wishlist.Toaster;
import com.pronofoot.teamazerty.app.R;
import com.pronofoot.teamazerty.app.core.Constants;
import com.pronofoot.teamazerty.app.util.SafeAsyncTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.InjectView;

import static android.R.layout.simple_dropdown_item_1line;
import static com.pronofoot.teamazerty.app.core.Constants.Http.URL_REGISTER;

/**
 * Created by PBVZ9205 on 23/05/2014.
 */
public class RegisterActivity extends AbstractPronofootActivity {

    private AccountManager accountManager;
    private SafeAsyncTask<Boolean> registerTask;

    @InjectView(R.id.register_username)
    EditText txtUsername;
    @InjectView(R.id.register_mail)
    AutoCompleteTextView txtEmail;
    @InjectView(R.id.register_password)
    EditText txtPassword;
    /*@InjectView(R.id.register_password2)
    EditText txtPassword2;*/

    @InjectView(R.id.register_lbl_mail)
    TextView lblMail;
    @InjectView(R.id.register_lbl_password)
    TextView lblPassword;
    @InjectView(R.id.register_lbl_username)
    TextView lblUsername;

    @InjectView(R.id.b_register)
    Button registerButton;
    @InjectView(R.id.register_loading)
    ProgressBar progressBar;

    /*@InjectView(R.id.register_password_error)
    TextView passwordErrorLabel;*/

    private final TextWatcher watcher = validationTextWatcher();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        accountManager = AccountManager.get(this);

        setContentView(R.layout.register_activity);

        txtEmail.setAdapter(new ArrayAdapter<String>(this,
                simple_dropdown_item_1line, userEmailAccounts()));

        //Watcher
        txtEmail.addTextChangedListener(watcher);
        txtPassword.addTextChangedListener(watcher);
        //txtPassword2.addTextChangedListener(watcher);
        txtUsername.addTextChangedListener(watcher);

        List<String> mails = userEmailAccounts();

        if (!mails.isEmpty()) {
            txtEmail.setText(mails.get(0));
        }
        hideProgress();
    }

    private List<String> userEmailAccounts() {
        final Account[] accounts = accountManager.getAccountsByType("com.google");
        final List<String> emailAddresses = new ArrayList<String>(accounts.length);
        for (final Account account : accounts) {
            emailAddresses.add(account.name);
        }
        return emailAddresses;
    }

    private TextWatcher validationTextWatcher() {
        return new TextWatcherAdapter() {
            public void afterTextChanged(final Editable gitDirEditText) {
                updateUIWithValidation();
            }

        };
    }

    public void doRegistration(View v) {
        final String username = txtUsername.getText().toString();
        final String password = txtPassword.getText().toString();
        final String email = txtEmail.getText().toString();
        final String user_lang = Locale.getDefault().getLanguage();
        if (isValid()) {
            showProgress();
            registerTask = new SafeAsyncTask<Boolean>() {

                public Boolean call() throws Exception {
                    Boolean res;

                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(URL_REGISTER);

                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                    nameValuePairs.add(new BasicNameValuePair(Constants.Param.PARAM_USERNAME, username));
                    nameValuePairs.add(new BasicNameValuePair(Constants.Param.PARAM_PASSWORD, password));
                    nameValuePairs.add(new BasicNameValuePair(Constants.Param.PARAM_USERMAIL, email));
                    nameValuePairs.add(new BasicNameValuePair(Constants.Param.PARAM_USERLANG, user_lang));
                    nameValuePairs.add(new BasicNameValuePair(Constants.Param.PARAM_ANDROID, "" + Build.VERSION.RELEASE));
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity entity = response.getEntity();
                    String dataRes = EntityUtils.toString(entity);

                    //Log.i("TA: retour", dataRes);

                    JSONObject jsonResponse = new JSONObject(dataRes);
                    if (jsonResponse.getBoolean("ok")) {
                        res = true;
                    } else {
                        res = false;
                        if (jsonResponse.getString("reason").equalsIgnoreCase(Constants.Extra.REASON_REGISTER_FAIL_MAIL)) {
                            Toaster.showLong(RegisterActivity.this,
                                    R.string.mail_error);
                        } else {
                            Toaster.showLong(RegisterActivity.this,
                                    R.string.username_error);
                        }
                    }

                    return res;
                }

                @Override
                protected void onException(Exception e) throws RuntimeException {
                    Throwable cause = e.getCause() != null ? e.getCause() : e;
                    String message = cause.getMessage();
                    Toaster.showLong(RegisterActivity.this, R.string.username_error);
                    Log.e("TA", message);
                }

                @Override
                public void onSuccess(Boolean authSuccess) {
                    onRegistrationResult(authSuccess);
                }

                @Override
                protected void onFinally() throws RuntimeException {
                    hideProgress();
                    registerTask = null;
                }
            };
            registerTask.execute();
        }
    }
    /**
     * Hide progress dialog
     */
    protected void hideProgress() {
        lblMail.setVisibility(View.VISIBLE);
        lblPassword.setVisibility(View.VISIBLE);
        lblUsername.setVisibility(View.VISIBLE);
        txtUsername.setVisibility(View.VISIBLE);
        txtPassword.setVisibility(View.VISIBLE);
        txtEmail.setVisibility(View.VISIBLE);
        registerButton.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
    }

    /**
     * Show progress dialog
     */
    protected void showProgress() {
        lblMail.setVisibility(View.INVISIBLE);
        lblPassword.setVisibility(View.INVISIBLE);
        lblUsername.setVisibility(View.INVISIBLE);
        txtUsername.setVisibility(View.INVISIBLE);
        txtPassword.setVisibility(View.INVISIBLE);
        txtEmail.setVisibility(View.INVISIBLE);
        registerButton.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }


    @Override
    protected void onResume() {
        super.onResume();
        updateUIWithValidation();
    }

    private void updateUIWithValidation() {
        registerButton.setEnabled(isValid());
        /*if (passwordMatch()) {
            passwordErrorLabel.setVisibility(View.INVISIBLE);
        } else {
            passwordErrorLabel.setVisibility(View.VISIBLE);
        }*/
    }

    private boolean isValid() {
        //Champs remplis
        final boolean populated =
                populated(txtEmail) &&
                        populated(txtPassword) &&
                        //populated(txtPassword2) &&
                        populated(txtUsername);

        //ça ne sert à rien d'aller plus loins
        return populated && passwordStrong();

    }

    /*private boolean passwordMatch() {
        return txtPassword.getText().toString().contentEquals(txtPassword2.getText().toString());
    }*/

    private boolean passwordStrong() {
        return txtPassword.length() >= 5;
    }

    private boolean populated(final EditText editText) {
        return editText.length() > 0;
    }

    /**
     * Called when the authentication process completes (see attemptLogin()).
     *
     * @param result
     */
    public void onRegistrationResult(boolean result) {
        if (result) {
            Toaster.showLong(RegisterActivity.this,
                    R.string.register_finish);
            finish();
        }
    }
}
