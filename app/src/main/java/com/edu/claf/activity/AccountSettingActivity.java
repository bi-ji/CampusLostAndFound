package com.edu.claf.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.edu.claf.BaseApplication;
import com.edu.claf.R;
import com.edu.claf.Utils.BitmapUtils;
import com.edu.claf.Utils.FileUtils;
import com.edu.claf.bean.User;
import com.edu.claf.view.ProgressView;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

public class AccountSettingActivity extends AppCompatActivity {


    private static final int REQUESTCODE_TAKE = 1;
    private static final int REQUESTCODE_PICK = 2;
    private static final int REQUESTCODE_CUTTING = 3;
    private static final int RESET_NICK_NAME = 5;
    private static final int REQUESTCODE_SCHOOL = 6;
    private static String ICON_FILE_LOCATION = null;
    private static String IMAGE_FILE_LOCATION = null;
    private static String PATH = null;
    private String fileName;


    @InjectView(R.id.nickname_useless)
    TextView tvNicknameUseless;
    @InjectView(R.id.nickname)
    TextView tvNickName;
    @InjectView(R.id.tv_change_password)
    TextView tvChangePassword;
    @InjectView(R.id.tv_telephone)
    TextView tvTelephone;
    @InjectView(R.id.tv_user_portrait)
    TextView tvUserPortrait;
    @InjectView(R.id.iv_portrait)
    ImageView ivPortrait;
    @InjectView(R.id.tv_as_change_place)
    TextView tvChangPlace;
    @InjectView(R.id.tv_as_change_school)
    TextView tvChangedSchool;
    private User mUser;

    SelectPicPopupWindow menuWindow;
    private Uri photoUri;
    private Uri imageUri;
    private File photoPath;
    private File imagePath;
    private ProgressDialog progressDialog;
    private FileOutputStream out;
    private String url;
    private FileOutputStream fos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_account_setting);
        BaseApplication.getApplication().addActivity(this);
        mUser = BmobUser.getCurrentUser(this, User.class);
        ButterKnife.inject(this);
        if (mUser != null) {
            ICON_FILE_LOCATION = FileUtils.getIconDir(mUser.getMobilePhoneNumber()).getAbsolutePath();
            IMAGE_FILE_LOCATION = FileUtils.getImageDir().getAbsolutePath();
            PATH = FileUtils.getIconDir(mUser.getMobilePhoneNumber()).getAbsolutePath();
            photoUri = Uri.parse(ICON_FILE_LOCATION);
            imageUri = Uri.parse(IMAGE_FILE_LOCATION);
            photoPath = FileUtils.getIconDir(mUser.getMobilePhoneNumber());
            imagePath = FileUtils.getImageDir();
            fileName = mUser.getMobilePhoneNumber();
            tvTelephone.setText(mUser.getMobilePhoneNumber());
            tvNickName.setText(mUser.getNickName());
            String realPath = PATH + File.separator + mUser.getMobilePhoneNumber() + ".jpg";
            ivPortrait.setImageBitmap(BitmapUtils.getBitmapFromSdCard(realPath, 2));
            tvChangedSchool.setText(mUser.getSchoolName());
        } else {
            tvTelephone.setText("");
        }
    }


    @OnClick(R.id.nickname_useless)
    public void resetNickName(View view) {
        String nickName = tvNickName.getText().toString().trim();
        Intent intent = new Intent(AccountSettingActivity.this, ResetNickNameActivity.class);
        intent.putExtra("nickname", nickName);
        startActivityForResult(intent, RESET_NICK_NAME);
    }

    @OnClick(R.id.tv_change_password)
    public void resetPassword(View view) {
        Intent intent = new Intent(AccountSettingActivity.this, ResetPasswordActivity.class);
        intent.putExtra("type","accountSettingReset");
        startActivity(intent);
    }


    @OnClick(R.id.tv_user_portrait)
    public void setUserPortrait(View view) {
        menuWindow = new SelectPicPopupWindow(AccountSettingActivity.this, itemsOnClick);
        menuWindow.showAtLocation(AccountSettingActivity.this.findViewById(R.id.account_setting),
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    @OnClick(R.id.tv_as_change_place)
    public void resetSchool(View view) {
        Intent intent = new Intent(this, ChooseProvinceActivity.class);
        intent.putExtra("type", "location_school");
        startActivityForResult(intent, REQUESTCODE_SCHOOL);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUESTCODE_TAKE:
                cropImageUri(photoUri, 300, 300, REQUESTCODE_CUTTING);
                break;
            case REQUESTCODE_PICK:
                if (data != null) {
                    Bitmap bitmap = data.getParcelableExtra("data");
                    Bitmap bitmap1 = data.getParcelableExtra("data");
                    saveBitmap(bitmap, photoPath, fileName + ".jpg");
                    saveBitmap(bitmap1, imagePath, fileName + ".jpg");
                    ivPortrait.setImageBitmap(bitmap);
                    uploadLogo(new File(FileUtils.getIconDir(mUser.getMobilePhoneNumber()), mUser.getMobilePhoneNumber() + ".jpg"));
                }
                break;
            case REQUESTCODE_CUTTING:
                if (data != null) {
                    Bitmap bitmap = decodeUriAsBitmap(photoUri);
                    Bitmap bitmap1 = decodeUriAsBitmap(imageUri);
                    saveBitmap(bitmap, photoPath, fileName + ".jpg");
                    saveBitmap(bitmap1, imagePath, fileName + ".jpg");
                    uploadLogo(new File(FileUtils.getIconDir(mUser.getMobilePhoneNumber()), mUser.getMobilePhoneNumber() + ".jpg"));
                    ivPortrait.setImageBitmap(bitmap);
                }
                break;
            case RESET_NICK_NAME:
                if (data != null) {
                    tvNickName.setText(data.getStringExtra("newNickName"));
                }
                break;
            case REQUESTCODE_SCHOOL:
                if (data != null) {
                    String school = data.getStringExtra("choose_school");
                    String province = data.getStringExtra("choose_province");
                    final ProgressView progressView = new ProgressView(this);
                    ProgressView.getProgressDialog(this, "重置中。。。");
                    User user = BmobUser.getCurrentUser(this, User.class);
                    user.setSchoolName(school);
                    user.setSchoolCity(province);
                    user.update(this, user.getObjectId(), new UpdateListener() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(AccountSettingActivity.this, "学校重置成功", Toast.LENGTH_SHORT).show();
                            ProgressView.progressDialogDismiss();
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            Toast.makeText(AccountSettingActivity.this, "学校重置失败", Toast.LENGTH_SHORT).show();
                            ProgressView.progressDialogDismiss();
                        }
                    });
                    tvChangedSchool.setText(school);
                }
                break;
        }
    }

    private View.OnClickListener itemsOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            menuWindow.dismiss();
            switch (view.getId()) {
                case R.id.btn_take_photo:
                    Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File outputImage2 = new File(photoPath, fileName + ".jpg");
                    try {
                        if (outputImage2.exists()) {
                            outputImage2.delete();
                        }
                        outputImage2.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    photoUri = Uri.fromFile(outputImage2);
                    takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    startActivityForResult(takeIntent, REQUESTCODE_TAKE);
                    break;
                case R.id.btn_pick_photo:
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
                    intent.setType("image/*");
                    intent.putExtra("crop", "true");
                    intent.putExtra("aspectX", 1);
                    intent.putExtra("aspectY", 1);
                    intent.putExtra("outputX", 200);
                    intent.putExtra("outputY", 200);
                    intent.putExtra("scale", true);
                    intent.putExtra("return-data", true);
                    intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
                    intent.putExtra("noFaceDetection", true);
                    startActivityForResult(intent, REQUESTCODE_PICK);
                    break;
                case R.id.btn_cancel:
                default:
                    break;
            }
        }
    };


    public Bitmap decodeUriAsBitmap(Uri uri) {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }

    private void cropImageUri(Uri uri, int outputX, int outputY, int requestCode) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", true);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra("return-data", true);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, requestCode);
    }

    public void saveBitmap(Bitmap bm, File path, String fileName) {
        File f = new File(path, fileName);
        File image = new File(imagePath, fileName);
        if (f.exists()) {
            f.delete();
        }
        try {
            out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    public void backPersonCentral(View view) {
        onBackPressed();
    }


    public void uploadLogo(final File userlogo) {
        deleteUserLogo(mUser);
        if (userlogo != null) {
            final BmobFile logo = new BmobFile(userlogo);
            final ProgressDialog progressDialog = new ProgressDialog(AccountSettingActivity.this);
            progressDialog.setMessage("正在设置。。。");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();
            logo.uploadblock(AccountSettingActivity.this, new UploadFileListener() {
                @Override
                public void onSuccess() {
                    url = logo.getFileUrl(AccountSettingActivity.this);
                    System.out.println(url + "dsfasdf-------------afsafas-----------------");
                    Toast.makeText(AccountSettingActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
                    updatePhotoUrl(mUser);
                    progressDialog.dismiss();
                }

                @Override
                public void onFailure(int i, String s) {

                }
            });
        }
    }

    private void deleteUserLogo(User user) {
        BmobFile file = new BmobFile();
        file.setUrl(user.getPhotoUrl());
        file.delete(this, new DeleteListener() {
            @Override
            public void onSuccess() {
                System.out.println("删除原头像成功");
            }

            @Override
            public void onFailure(int i, String s) {
                System.out.println("删除原头像失败" + s);
            }
        });
    }

    public void updatePhotoUrl(User user) {
        User updateUser = new User();
        updateUser.setPhotoUrl(url);
        updateUser.update(this, user.getObjectId(), new UpdateListener() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onFailure(int i, String s) {

            }
        });
    }

    public void saveUserToJson(User user) {
        Gson gson = new Gson();
        String userJson = gson.toJson(user);
        File userDir = FileUtils.getUserDir(user);
        if (!userDir.exists()) {
            userDir.mkdir();
        }
        File file = new File(FileUtils.getCacheDir(user), user.getMobilePhoneNumber() + ".json");
        if (file.exists()) {
            file.delete();
        }
        PrintStream out = null;
        try {
            out = new PrintStream(new FileOutputStream(file));
            out.print(userJson);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
