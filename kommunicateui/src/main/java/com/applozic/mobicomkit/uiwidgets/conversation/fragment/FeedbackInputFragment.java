package com.applozic.mobicomkit.uiwidgets.conversation.fragment;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.applozic.mobicomkit.uiwidgets.R;
import com.applozic.mobicommons.commons.core.utils.Utils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * fragment for the feedback input form
 * @author shubham
 * @date july 19
 */
public class FeedbackInputFragment extends Fragment implements View.OnClickListener{

    private static final String TAG = "FeedbackInputFragment";

    EditText editTextFeedbackComment;
    Button buttonSubmitFeedback;
    Button buttonCloseFragment;

    FeedbackRatingGroup feedbackRatingGroup;

    FeedbackFragmentListener feedbackFragmentListener;

    public static final int RATING_POOR = 1;
    public static final int RATING_AVERAGE = 2;
    public static final int RATING_GOOD = 3;

    //using IntDef to replace enum
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({RATING_POOR, RATING_AVERAGE, RATING_GOOD})
    public @interface Rating {}

    @Rating
    public int getRatingLevel() {
        return ratingLevel;
    }

    public void setRatingLevel(@Rating int ratingLevel) {
        this.ratingLevel = ratingLevel;
    }

    @Rating private int ratingLevel;

    public void setFeedbackFragmentListener(FeedbackFragmentListener feedbackFragmentListener) {
        this.feedbackFragmentListener = feedbackFragmentListener;
    }

    public static String getTAG() {
        return TAG;
    }

    public FeedbackInputFragment() {
        super();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (!FeedbackFragmentListener.class.isAssignableFrom(MobiComConversationFragment.class)) {
            Utils.printLog(context, TAG, "Implement FeedbackFragmentListener in your parent fragment.");
            throw new ClassCastException("Implement FeedbackFragmentListener in your parent fragment.");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.feedback_rating_layout, container, false);

        editTextFeedbackComment = view.findViewById(R.id.idEditTextFeedback);
        buttonSubmitFeedback = view.findViewById(R.id.idButtonSubmit);

        buttonCloseFragment = view.findViewById(R.id.idCloseFeedbackFragment);

        editTextFeedbackComment.setVisibility(View.GONE);

        //selected
        Drawable[] buttonDrawablesSelected = new Drawable[3];
        buttonDrawablesSelected[0] = getResources().getDrawable(R.drawable.ic_sad_1);
        buttonDrawablesSelected[1] = getResources().getDrawable(R.drawable.ic_confused);
        buttonDrawablesSelected[2] = getResources().getDrawable(R.drawable.ic_happy);
        //not selected
        Drawable[] buttonDrawablesUnselected = new Drawable[3];
        buttonDrawablesUnselected[0] = getResources().getDrawable(R.drawable.ic_sad_1_grey);
        buttonDrawablesUnselected[1] = getResources().getDrawable(R.drawable.ic_confused_grey);
        buttonDrawablesUnselected[2] = getResources().getDrawable(R.drawable.ic_happy_grey);

        feedbackRatingGroup = new FeedbackRatingGroup(3, buttonDrawablesSelected, buttonDrawablesUnselected);

        feedbackRatingGroup.createViewForRatingValue(view, 1, R.id.idButtonPoor, R.id.idTextPoor);
        feedbackRatingGroup.createViewForRatingValue(view, 2, R.id.idButtonAverage, R.id.idTextAverage);
        feedbackRatingGroup.createViewForRatingValue(view, 3, R.id.idButtonGood, R.id.idTextGood);

        buttonSubmitFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ratingLevel == 0) {
                    setRatingLevel(RATING_AVERAGE);
                }
                String feedbackComment = editTextFeedbackComment.getText().toString().trim();
                feedbackFragmentListener.onFeedbackSubmitButtonPressed(getRatingLevel(), feedbackComment);
                getFragmentManager().popBackStack();
            }
        });

        buttonCloseFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().popBackStack();
            }
        });

        return view;
    }

    /**
     * toggle button as selected or deselected
     * @param select true or false
     * @param feedbackRating the views to toggle
     */
    void toggleRatingButtonSelected(boolean select, FeedbackRatingGroup.FeedbackRating feedbackRating) {
        //show the feedback comment input edit text, if not already visible
        if(editTextFeedbackComment.getVisibility()==View.GONE) {
            editTextFeedbackComment.setVisibility(View.VISIBLE);
        }

        //set drawable
        feedbackRatingGroup.setDrawableForValue(select, feedbackRating.ratingValue);

        //set visibility and size
        if(select) {
            feedbackRating.ratingButton.setScaleX(1f);
            feedbackRating.ratingButton.setScaleY(1f);
            feedbackRating.feedbackTextView.setVisibility(View.VISIBLE);
        } else {

            feedbackRating.ratingButton.setScaleX(0.8f);
            feedbackRating.ratingButton.setScaleY(0.8f);
            feedbackRating.feedbackTextView.setVisibility(View.GONE);
        }
    }

    //when one of the rating buttons is clicked
    @Override
    public void onClick(View view) {
        Integer buttonTag = (Integer) view.getTag();

        for(FeedbackRatingGroup.FeedbackRating feedbackRating : feedbackRatingGroup.feedbackRating) {
            Integer iterationButtonTag = (Integer) feedbackRating.ratingButton.getTag();
            if(iterationButtonTag.intValue() == buttonTag.intValue()) {
                setRatingLevel(buttonTag);
                toggleRatingButtonSelected(true, feedbackRating);
            }
            else
                toggleRatingButtonSelected(false, feedbackRating);
        }
    }

    /**
     * class for the feedback rating input buttons and text views
     */
    public class FeedbackRatingGroup {
        Drawable[] selectedDrawable;
        Drawable[] unSelectedDrawable;
        FeedbackRating[] feedbackRating;
        int noOfRatingElements;

        public FeedbackRatingGroup(int noOfRatings, Drawable[] selectedDrawable, Drawable[] unSelectedDrawable) {
            noOfRatingElements = noOfRatings;
            this.selectedDrawable = selectedDrawable;
            this.unSelectedDrawable = unSelectedDrawable;
            feedbackRating = new FeedbackRating[noOfRatingElements];
        }

        /**
         * class for a single rating button, text view and their properties
         */
        class FeedbackRating {
            Button ratingButton;
            TextView feedbackTextView;
            int ratingValue;
        }

        void setDrawableForValue(boolean select, int value) {
            if(select) {
                feedbackRating[value - 1].ratingButton.setBackground(selectedDrawable[value - 1]);
            } else {
                feedbackRating[value - 1].ratingButton.setBackground(unSelectedDrawable[value - 1]);
            }
        }

        /**
         * this function will initialize a button and a text view with the properties provided and add it tp the
         * respective button and text view arrays
         * will also set a listener on it
         * @param rootView the parent root view
         * @param value the rating value (work as index to array)
         * @param buttonResId the id of the button in the layout file
         * @param textViewResId the id of the text view in the layout file
         */
        public void createViewForRatingValue(View rootView, int value, @IdRes int buttonResId, @IdRes int textViewResId) {
            feedbackRating[value - 1] = new FeedbackRating();

            feedbackRating[value - 1].ratingButton = rootView.findViewById(buttonResId);
            feedbackRating[value - 1].feedbackTextView = rootView.findViewById(textViewResId);

            feedbackRating[value - 1].ratingButton.setTag(value);
            feedbackRating[value - 1].ratingButton.setOnClickListener(FeedbackInputFragment.this);
            feedbackRating[value - 1].ratingValue = value;
            feedbackRating[value - 1].ratingButton.setScaleX(0.8f);
            feedbackRating[value - 1].ratingButton.setScaleY(0.8f);

            feedbackRating[value - 1].feedbackTextView.setVisibility(View.GONE);
        }
    }



    public interface FeedbackFragmentListener {
        void onFeedbackSubmitButtonPressed(int rating, String feedback);
    }
}
