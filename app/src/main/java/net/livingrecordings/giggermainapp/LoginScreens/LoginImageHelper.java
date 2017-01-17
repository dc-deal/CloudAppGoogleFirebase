/*
 * Copyright 2014 Julian Shen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.livingrecordings.giggermainapp.LoginScreens;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import net.livingrecordings.giggermainapp.R;
import net.livingrecordings.giggermainapp.giggerMainClasses.helperClasses.BitmapConverterHelper;
import net.livingrecordings.giggermainapp.giggerMainClasses.helperClasses.GiggerIntentHelperClass;

/**
 * Created by julian on 13/6/21.
 */
public class LoginImageHelper implements Transformation {


    Activity context1;
    String providerId,profileuid;

    public LoginImageHelper fillImage(final Activity context, ImageView imgView, Uri urlForPhoto){
        BitmapConverterHelper bh = new BitmapConverterHelper();
        int height = Math.round(context.getResources().getDimension(R.dimen.BandList_profileColumn));
        Bitmap loadbmp = transform(bh.resizeBitmap(context.getResources(), R.drawable.defprofilepic, height));
        Drawable loadPic = new BitmapDrawable(context.getResources(), loadbmp);
        Bitmap errbmp = transform(bh.resizeBitmap(context.getResources(), R.drawable.erroricon, height));
        Drawable errorPic = new BitmapDrawable(context.getResources(), errbmp);
        Picasso.with(context)
                .load(urlForPhoto)
                .placeholder(loadPic )// //R.drawable.progress_animation
                .error(errorPic)
                .transform(new LoginImageHelper()).into(imgView);
        return this;
    }


    public void fillLoginInfoField(final Activity context, View rootView) {
        context1 = context;
        LinearLayout signInUserLayout;
        TextView textUser, hintText;
        ImageView imgUser;
        signInUserLayout = (LinearLayout) rootView.findViewById(R.id.show_loggedinUser);
        imgUser = (ImageView) rootView.findViewById(R.id.contlist_imageview_users);
        textUser = (TextView) rootView.findViewById(R.id.contlist_textview_users);
        hintText = (TextView) rootView.findViewById(R.id.contlist_textview_uemails);
        // show_loggedinUser Layout muss mitgeliefert werden!!
        if (signInUserLayout != null) {
            // das user layout verändern, nach meinen wünsceh, es soll nun den ocntent height wrappen.
            ViewGroup.LayoutParams params = signInUserLayout.getLayoutParams();
            params.width = ActionBar.LayoutParams.MATCH_PARENT;
            params.height = ActionBar.LayoutParams.WRAP_CONTENT;
            signInUserLayout.setLayoutParams(params);
            signInUserLayout.setClickable(true);// sofort klickbar machen um auf den account zuzugreifen...

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            // wenn das fenster das erste mal aufgerufen wird...
            if (user != null) {
                for (UserInfo profile : user.getProviderData()) {
                    // Id of the provider (ex: google.com)
                    providerId = profile.getProviderId();
                    profileuid = profile.getUid();
                }
                // Name, email address, and profile photo Url
                String name = user.getDisplayName();
                String email = user.getEmail();
                Uri photoUrl = user.getPhotoUrl();
                fillImage(context,imgUser,photoUrl);
                //   Picasso.with(getActivity()).load(gpersonPhoto).into(imgUser);
                textUser.setText(name);
                hintText.setText(email);
                if (user.isAnonymous()) {
                    textUser.setText("Automatisch angemeldet");
                    hintText.setText("zz. noch kein Account vorhanden.");
                }

            }

            // den angemeldet als text...
            TextView tv = (TextView) rootView.findViewById(R.id.idauth_choose_method_yourlogin);
            String ylogm = context.getString(R.string.auth_choose_method_yourlogin);
            tv.setText(ylogm);
            if (providerId.equals("google.com")) {
                tv.setText(ylogm+" "+context.getString(R.string.auth_choose_method_yourlogin_google));
            } else {
                if (!user.isAnonymous()) {
                    tv.setText(ylogm+ " "+ context.getString(R.string.auth_choose_method_yourlogin_email));
                } else {
                    tv.setText(ylogm+ " "+context.getString(R.string.auth_choose_method_yourlogin_anonymous));
                }
            }
            // ende
            signInUserLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // beim klick aufs profilbild sollte die google plus app aufgehen mit dem Profil.
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    // wenn das fenster das erste mal aufgerufen wird...
                    if (user != null) {
                        if (providerId.equals("google.com")) {
                            context1.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://plus.google.com/" + profileuid + "/posts")));
                        } else {
                            if (!user.isAnonymous()) {
                                // Profil per EMAIL ÖFFNEN!!!
                                GiggerIntentHelperClass ih = new GiggerIntentHelperClass(context1);
                                ih.intentShowFBContasct(FirebaseAuth.getInstance().getCurrentUser());
                            } else {
                                // nicht zulässig!
                                Toast.makeText(context1,context1.getString(R.string.auth_failed_anonymous_clickprofile),Toast.LENGTH_LONG);
                            }
                        }
                    }
                }
            });
        }
    }


    @Override
    public Bitmap transform(Bitmap source) {
        int size = Math.min(source.getWidth(), source.getHeight());

        int x = (source.getWidth() - size) / 2;
        int y = (source.getHeight() - size) / 2;

        Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
        if (squaredBitmap != source) {
            source.recycle();
        }

        Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        BitmapShader shader = new BitmapShader(squaredBitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
        paint.setShader(shader);
        paint.setAntiAlias(true);

        float r = size / 2f;
        canvas.drawCircle(r, r, r, paint);

        squaredBitmap.recycle();
        return bitmap;
    }

    @Override
    public String key() {
        return "circle";
    }


}
