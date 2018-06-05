/*
 * Copyright 2018 Realm Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.realm.todo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import io.realm.ObjectServerError;
import io.realm.SyncCredentials;
import io.realm.SyncUser;

import static io.realm.todo.Constants.AUTH_URL;

/*
Now locate the WelcomeActivity class. This activity is responsible of authenticating a user in
order to access your Realm Cloud instance. This is done in the attemptLogin method by using the
provided "nickname". Realm Cloud's "nickname" authentication provider is an excellent development
credential for you to quickly get started with Realm Sync without the need to remember any credentials.
*/

/*In production you will most likely be using a password based provider like usernamePassword or OAuth based like facebook or Google.*/

public class WelcomeActivity extends AppCompatActivity {

    private EditText mNicknameTextView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // Set up the login form.
        mNicknameTextView = findViewById(R.id.nickname);
        Button loginButton = findViewById(R.id.login_button);
        loginButton.setOnClickListener(view -> attemptLogin());
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);


        /*
        After a successful login, the SyncUser is persisted internally,
        there's no need to login again on the next app startup. We do check
        in onCreate if there's already a SyncUser. If we have one we do not
        attempt to login a user, instead we navigate directly to the next Activity.
        Add the following right before setting up the login form:
        */
        //todo (8) add SyncUser check
        if (SyncUser.current() != null) {
            goToItemsActivity();
        }
    }

    private void attemptLogin() {
        // Reset errors.
        mNicknameTextView.setError(null);
        // Store values at the time of the login attempt.
        String nickname = mNicknameTextView.getText().toString();
        showProgress(true);

        //todo (7) add login logic
        SyncCredentials credentials = SyncCredentials.nickname(nickname, false);

        SyncUser.logInAsync(credentials, AUTH_URL, new SyncUser.Callback<SyncUser>() {
            @Override
            public void onSuccess(SyncUser user) {
                showProgress(false);
                goToItemsActivity();
            }

            @Override
            public void onError(ObjectServerError error) {
                showProgress(false);
                mNicknameTextView.setError("Uh oh something went wrong! (check your logcat please)");
                mNicknameTextView.requestFocus();
                Log.e("Login error", error.toString());
            }
        });
    }



    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void goToItemsActivity() {
        Intent intent = new Intent(WelcomeActivity.this, ItemsActivity.class);
        startActivity(intent);
    }
}
