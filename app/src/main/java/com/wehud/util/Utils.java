package com.wehud.util;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.wehud.R;
import com.wehud.dialog.TextDialogFragment;
import com.wehud.model.CreatedResponse;
import com.wehud.model.Image;
import com.wehud.model.Reward;
import com.wehud.model.Score;
import com.wehud.model.Status;
import com.wehud.model.Tag;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public final class Utils {

    private Utils() {}

    public static void toast(final Context context, final int id, final Object... formatArgs) {
        Toast.makeText(
                context,
                context.getResources().getString(id, formatArgs),
                Toast.LENGTH_SHORT
        ).show();
    }

    public static void toast(final Context context, final int id) {
        Toast.makeText(
                context,
                context.getString(id),
                Toast.LENGTH_SHORT
        ).show();
    }

    public static boolean isNotEmpty(final Collection collection) {
        return collection != null && collection.isEmpty();
    }

    public static void expand(final View v) {
        v.measure(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);

        final Animation anim = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? WindowManager.LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // Animation duration is set to 1 dp per millisecond.
        anim.setDuration(
                (int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density)
        );
        v.startAnimation(anim);
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        final Animation anim = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height =
                            initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // Animation duration is set to 1 dp per millisecond.
        anim.setDuration(
                (int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(anim);
    }

    public static String isoDateTimeStringToLocalDateTimeString(final String iso) {
        SimpleDateFormat sdf = new SimpleDateFormat(
                Constants.ISO_8601_PATTERN,
                Locale.getDefault()
        );
        Date d = null;
        try {
            d = sdf.parse(iso);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        sdf.applyPattern(Constants.LOCAL_PATTERN_DATETIME);

        return sdf.format(d);
    }

    public static String localDateTimeStringToIsoDateTimeString(final String local) {
        SimpleDateFormat sdf = new SimpleDateFormat(
                Constants.LOCAL_PATTERN_DATETIME,
                Locale.getDefault()
        );
        Date d = null;
        try {
            d = sdf.parse(local);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        sdf.applyPattern(Constants.ISO_8601_PATTERN);

        return sdf.format(d);
    }

    public static String timestampToLocalDateString(final long timestamp) {
        final Timestamp tstp = new Timestamp(timestamp);
        DateFormat df = new SimpleDateFormat(Constants.LOCAL_PATTERN_DATE, Locale.getDefault());
        return df.format(new Date(tstp.getTime()));
    }

    public static Status getStatus(final Context context, final int status) {
        StringBuilder sb = new StringBuilder();
        int resId;

        switch (status) {
            case 0:
                resId = R.drawable.ic_status_released;
                sb.append(context.getString(R.string.status_released));
                break;
            case 2:
                resId = R.drawable.ic_status_alpha;
                sb.append(context.getString(R.string.status_alpha));
                break;
            case 3:
                resId = R.drawable.ic_status_beta;
                sb.append(context.getString(R.string.status_beta));
                break;
            case 4:
                resId = R.drawable.ic_status_early_access;
                sb.append(context.getString(R.string.status_earlyAccess));
                break;
            case 5:
                resId = R.drawable.ic_status_offline;
                sb.append(context.getString(R.string.status_offline));
                break;
            case 6:
                resId = R.drawable.ic_status_cancelled;
                sb.append(context.getString(R.string.status_cancelled));
                break;
            default:
                resId = R.drawable.ic_status_unknown;
                sb.append(context.getString(R.string.status_unknown));
                break;
        }

        return new Status(resId, sb.toString());
    }

    public static Tag getTag(final int tag) {
        int code, icon;

        switch (tag) {
            case Constants.TAG_LIVE:
                code = Constants.TAG_LIVE;
                icon = R.drawable.ic_live;
                break;
            case Constants.TAG_MEETUP:
                code = Constants.TAG_MEETUP;
                icon = R.drawable.ic_meetup;
                break;
            case Constants.TAG_YOUTUBE:
                code = Constants.TAG_YOUTUBE;
                icon = R.drawable.ic_youtube;
                break;
            case Constants.TAG_BIRTHDAY:
                code = Constants.TAG_BIRTHDAY;
                icon = R.drawable.ic_birthday;
                break;
            default:
                code = Constants.TAG_EVENT;
                icon = R.drawable.ic_event;
                break;
        }

        return new Tag(code, icon);
    }

    public static TextView getFirstInvalidField(TextView... views) {
        TextView focusView = null;
        for (TextView view : views) {
            view.setError(null);
            if (TextUtils.isEmpty(view.getText().toString())) {
                focusView = view;
                break;
            }
        }

        return focusView;
    }

    public static void clearText(TextView... views) {
        for (TextView view : views) view.setText(null);
    }

    public static void putStringListIntoTextView(TextView txt, final List<String> strings) {
        final int len = strings.size();
        for (int i = 0; i < len; ++i) {
            if (i < len - 1) txt.append(strings.get(i) + ", ");
            else txt.append(strings.get(i));
        }
    }

    public static ArrayList<Image> getDefaultAvatars() {
        ArrayList<Image> images = new ArrayList<>();
        for (final String avatar : Constants.AVATARS) {
            images.add(new Image(avatar, 0));
        }

        return images;
    }

    public static Score getScore(final int score) {
        int level, rankIndex;
        if (score >= 0 && score < 100) level = (score / 10) + 1;
        else level = score / 100;
        rankIndex = level - 1;

        StringBuilder rank = new StringBuilder();
        if (score > 999) {
            final int len = String.valueOf(level).length();
            rankIndex = (level / ((len - 1) * 10)) - 1;
            rank.append(Constants.RANKS[rankIndex]);

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < len - 1; ++i) sb.append('+');

            rank.append(sb.toString());
        }
        else rank.append(Constants.RANKS[rankIndex]);

        return new Score(level, rank.toString());
    }

    public static Reward getNestedReward(final String payload) {
        final CreatedResponse response = GsonUtils.getInstance().fromJson(
                payload,
                CreatedResponse.class
        );
        return response.getReward();
    }

    public static void generateRewardDialog(
            final Context context,
            final FragmentManager manager,
            final TextDialogFragment.OnTextDialogDismissOkListener listener,
            final Reward reward,
            final int id
    ) {
        final List<String> entities = reward.getEntities();
        StringBuilder entitiesString = new StringBuilder();
        if (entities.size() == 1) entitiesString.append(entities.get(0));
        else if (entities.size() == 2) {
            String toAppend = "a " + entities.get(0) + ' ' +
                    context.getString(R.string.or) + " a " +
                    entities.get(1);
            entitiesString.append(toAppend);
        }
        else {
            final int size = entities.size();
            for (int i = 0; i < size - 2; ++i) {
                final String toAppend = entities.get(i) + ", ";
                entitiesString.append(toAppend);
            }

            final String toAppend = entities.get(size - 2) + ' ' +
                    context.getString(R.string.or) + ' ' +
                    entities.get(entities.size() - 1);
            entitiesString.append(toAppend);
        }

        final int action = reward.getAction();

        final String title = context.getString(R.string.dialogTitle_reward);
        StringBuilder message = new StringBuilder();
        switch (action) {
            case 0:
                message.append(context.getResources().getString(
                        R.string.dialogText_reward,
                        action,
                        context.getResources().getString(R.string.new_publication, entitiesString),
                        reward.getBonus()
                ));
                break;
            case 1:
                message.append(context.getResources().getString(
                        R.string.dialogText_reward,
                        reward.getScore(),
                        context.getResources().getString(R.string.new_binding, entitiesString),
                        reward.getBonus()
                ));
                break;
            case 2:
                message.append(context.getResources().getString(
                        R.string.dialogText_reward,
                        reward.getScore(),
                        context.getResources().getString(R.string.new_follow, entitiesString),
                        reward.getBonus()
                ));
                break;
            default:
                return;
        }

        TextDialogFragment.generate(manager, listener, title, message.toString(), id);
    }

    public static boolean isConnectedUser(final Context context, final String userId) {
        final String connectedId = PreferencesUtils.get(context, Constants.PREF_USER_ID);
        return !TextUtils.isEmpty(connectedId) && connectedId.equals(userId);

    }

    public static String generateId(final Random rng) {
        return generateString(rng, Constants.CHARACTERS, 24);
    }

    /**
     * Helper method to call a Picasso static instance to load images.
     *
     * @param context the {@link Context} of the application
     * @param imgUrl  a {@link String} object representing the URL of the image
     * @param iv      an {@link ImageView} in which to load the image
     */
    public static void loadImage(final Context context, final String imgUrl, final ImageView iv) {
        Picasso.with(context).load(imgUrl).into(iv);
    }

    /**
     * Helper method to call a Picasso static instance to load images and resize them.
     *
     * @param context the {@link Context} of the application
     * @param imgUrl  a {@link String} object representing the URL of the image
     * @param iv      an {@link ImageView} in which to load the image
     * @param i       a value in pixels used to resize the image
     */
    public static void loadImage(
            final Context context,
            final String imgUrl,
            final ImageView iv,
            final int i)
    {
        loadImage(context, imgUrl, iv, i, i);
    }

    /**
     * Helper method to call a Picasso static instance to load images and resize them.
     *
     * @param context the {@link Context} of the application
     * @param imgUrl  a {@link String} object representing the URL of the image
     * @param iv      an {@link ImageView} in which to load the image
     * @param i       a value in pixels used as the width of the image
     * @param i2      a value in pixels used as the height of the image
     */
    protected static void loadImage(
            final Context context,
            final String imgUrl,
            final ImageView iv,
            final int i,
            final int i2)
    {
        Picasso.with(context).load(imgUrl).resize(i, i2).into(iv);
    }

    private static String generateString(
            final Random rng,
            final String characters,
            final int length)
    {
        char[] text = new char[length];
        for (int i = 0; i < length; i++)
        {
            text[i] = characters.charAt(rng.nextInt(characters.length()));
        }
        return new String(text);
    }
}
