package com.nullpointerexception.cicerone.custom_views;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.nullpointerexception.cicerone.R;
import com.nullpointerexception.cicerone.components.ImageFetcher;
import com.nullpointerexception.cicerone.components.Itinerary;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 *      ItineraryView
 *
 *      View containing information of an itinerary.
 *
 *      @author Luca
 */
public class ItineraryView extends FrameLayout
{

    /*
            Inner views
     */
    private View buttonsContainer, editButton, deleteButton;
    private TextView city, date, meeting, cicerone;
    private ImageView imageView;

    /*
            Vars
     */
    private OnViewClickListener onViewClickListener;
    private OnEditButtonClickListener onEditButtonClickListener;
    private OnDeleteButtonClickListener onDeleteButtonClickListener;

    /**   Indicates if view is in a collapsed state */
    private boolean isCollapsed = true;
    /**   Indicates if view has stopped scrollbars of its parents */
    private boolean stoppedScrollbars = false;
    /**   Indicates if this view is last element of a list */
    private boolean isLastElement = false;

    @SuppressLint("ClickableViewAccessibility")
    public ItineraryView(@NonNull Context context)
    {
        super(context);
        addView( inflate(context, R.layout.itinerary_layout, null) );

        setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        city = findViewById(R.id.cityLabel);
        date = findViewById(R.id.dateLabel);
        meeting = findViewById(R.id.meetingLabel);
        cicerone = findViewById(R.id.ciceroneLabel);
        buttonsContainer = findViewById(R.id.buttonsContainer);
        editButton = findViewById(R.id.editButton);
        deleteButton = findViewById(R.id.deleteButton);
        imageView = findViewById(R.id.itineraryBack);

        setOnTouchListener((view, motionEvent) ->
        {

            switch (motionEvent.getAction())
            {
                case MotionEvent.ACTION_DOWN:
                    setScaleX(0.98f);
                    setScaleY(0.98f);
                    setAlpha(0.85f);

                    break;

                case MotionEvent.ACTION_CANCEL:

                    setScaleX(1f);
                    setScaleY(1f);
                    setAlpha(1f);

                    if( ! isCollapsed)
                        collapse();

                    break;

                case MotionEvent.ACTION_MOVE:
                    checkTouchEventsWith(motionEvent);
                    break;

                case MotionEvent.ACTION_UP:
                    setScaleX(1f);
                    setScaleY(1f);
                    setAlpha(1f);

                    checkTouchEventsWith(motionEvent);

                    if(isCollapsed)
                    {
                        if (onViewClickListener != null)
                            onViewClickListener.onViewClick();

                        performClick();
                    }
                    else
                        collapse();

                    break;
            }

            return false;
        });

        setOnLongClickListener(view ->
        {
            if( isCollapsed)
                expand();

            return false;
        });

        editButton.setOnTouchListener((view, motionEvent) ->
        {
            switch (motionEvent.getAction())
            {
                case MotionEvent.ACTION_MOVE:
                    editButton.setScaleX(1.1f);
                    editButton.setScaleY(1.1f);
                    break;

                case MotionEvent.ACTION_UP:
                    if(onEditButtonClickListener != null)
                        onEditButtonClickListener.onEditButtonClick();
                    break;
            }

            return false;
        });

        deleteButton.setOnTouchListener((view, motionEvent) ->
        {
            switch (motionEvent.getAction())
            {
                case MotionEvent.ACTION_MOVE:
                    deleteButton.setScaleX(1.1f);
                    deleteButton.setScaleY(1.1f);
                    break;

                case MotionEvent.ACTION_UP:
                    if(onDeleteButtonClickListener != null)
                        onDeleteButtonClickListener.onDeleteButtonClick();
                    break;
            }

            return false;
        });

    }

    /**
     *      Calculate touch events based on its coordinates and redirect events to right views.
     *
     *      @param motionEvent  Motion event generated by user.
     */
    private void checkTouchEventsWith(MotionEvent motionEvent)
    {
        int x = (int) motionEvent.getX() % getMeasuredWidth();
        int y = (int) motionEvent.getY() % getMeasuredHeight();

        /*
                Edit button
         */
        Rect editRect = new Rect(editButton.getLeft(),
                editButton.getTop() + ((View) editButton.getParent()).getTop(),
                editButton.getRight(),
                editButton.getBottom() + ((View) editButton.getParent()).getTop());

        if(editRect.contains(x, y))
            editButton.dispatchTouchEvent(motionEvent);
        else
        {
            editButton.setScaleX(1f);
            editButton.setScaleY(1f);
        }

        /*
                Delete Button
         */
        Rect deleteRect = new Rect(deleteButton.getLeft(),
                deleteButton.getTop() + ((View) deleteButton.getParent()).getTop(),
                deleteButton.getRight(),
                deleteButton.getBottom() + ((View) deleteButton.getParent()).getTop());

        if(deleteRect.contains(x, y))
            deleteButton.dispatchTouchEvent(motionEvent);
        else
        {
            deleteButton.setScaleX(1f);
            deleteButton.setScaleY(1f);
        }
    }

    /**
     *      Set views content based on informations of itinerary passed.
     *
     *      @param itinerary    Itinerary that gives informations.
     */
    public void setFrom(Itinerary itinerary)
    {
        city.setText(itinerary.getLocation());

        //Change date format
        try
        {
            Date day = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).parse(itinerary.getDate());
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String dayString = formatter.format(day);
            date.setText(dayString);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        meeting.setText(itinerary.getMeetingPlace() + " - " + itinerary.getMeetingTime());
        cicerone.setVisibility(GONE);

        View layout = findViewById(R.id.layout);

        layout.post(() ->
        {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT,
                    layout.getHeight());
            params.addRule(RelativeLayout.RIGHT_OF, findViewById(R.id.guideLine).getId());
            imageView.setLayoutParams(params);
        });

        //  TODO: Add check time

        String keyword = itinerary.getLocation() + " " + getResources().getString(R.string.city);

        if(itinerary.getLocation().trim().equalsIgnoreCase("bitritto"))
            keyword = "corvo gigante bitritto";

        new ImageFetcher().findSubject(keyword,
        new ImageFetcher.OnImageFoundListener()
        {
            @Override
            public void onImageFound(String url)
            {
                Log.i("ImageFetcher", "Success! Link -->" + url);

                if(city.getContext() instanceof Activity)
                {
                    ((Activity) city.getContext()).runOnUiThread(() ->
                            Glide.with(city.getContext())
                            .load(url)
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .into(imageView));
                }
            }

            @Override
            public void onError()
            {
                Log.i("ImageFetcher", "Errore.");
            }
        });
    }

    public void setAsLastElement(boolean value)
    {
        isLastElement = value;
    }

    public boolean isLastElement() { return isLastElement; }

    /**
     *      Hides edit and delete button with animations, after set state as collapsed.
     */
    public void collapse()
    {
        if( isCollapsed)
            return;

        ViewGroup parent = ((ViewGroup) getParent());
        if(parent != null && stoppedScrollbars)
        {
            parent.requestDisallowInterceptTouchEvent(false);
            stoppedScrollbars = false;
        }


        if(isLastElement)
        {
            editButton.animate().translationYBy(200).setDuration(200)
                    .setInterpolator(new DecelerateInterpolator()).start();
            deleteButton.animate().translationYBy(200).setDuration(200).start();
        }
        else
        {
            editButton.animate().translationYBy(-200).setDuration(200)
                    .setInterpolator(new DecelerateInterpolator()).start();
            deleteButton.animate().translationYBy(-200).setDuration(200).start();
        }

        new Handler().postDelayed(() ->
        {

            if(isLastElement)
            {
                findViewById(R.id.cardItinerary).setVisibility(VISIBLE);

                editButton.animate().translationYBy(-200).setDuration(0).start();
                deleteButton.animate().translationYBy(-200).setDuration(0)
                        .setInterpolator(new AccelerateInterpolator()).start();
            }
            else
            {
                editButton.animate().translationYBy(200).setDuration(0).start();
                deleteButton.animate().translationYBy(200).setDuration(0)
                        .setInterpolator(new AccelerateInterpolator()).start();
            }

            buttonsContainer.setVisibility(GONE);
            isCollapsed = true;
        }, 250);

    }

    /**
     *      Shoss edit and delete button with animations, after set state as not collapsed.
     */
    public void expand()
    {
        if( ! isCollapsed)
            return;

        ViewGroup parent = ((ViewGroup) getParent());
        if(parent != null)
        {
            parent.requestDisallowInterceptTouchEvent(true);
            stoppedScrollbars = true;
        }

        buttonsContainer.setVisibility(VISIBLE);
        isCollapsed = false;

        if(isLastElement)
        {
            findViewById(R.id.cardItinerary).setVisibility(GONE);

            editButton.animate().translationY(200).setDuration(0).start();
            deleteButton.animate().translationY(200).setDuration(0).start();

            editButton.animate().translationYBy(-200).setDuration(200)
                    .setInterpolator(new DecelerateInterpolator()).start();
            deleteButton.animate().translationYBy(-200).setDuration(200)
                    .setInterpolator(new AccelerateInterpolator()).start();
        }
        else
        {
            editButton.animate().translationY(-200).setDuration(0).start();
            deleteButton.animate().translationY(-200).setDuration(0).start();

            editButton.animate().translationYBy(200).setDuration(200)
                    .setInterpolator(new DecelerateInterpolator()).start();
            deleteButton.animate().translationYBy(200).setDuration(200)
                    .setInterpolator(new AccelerateInterpolator()).start();
        }


    }

    public boolean isCollapsed() { return isCollapsed; }

    /**   Permit to implement callback method for the click on this view event.  */
    public interface OnViewClickListener { void onViewClick(); }

    /**   Permit to implement callback method for the click on edit button event.  */
    public interface OnEditButtonClickListener { void onEditButtonClick(); }

    /**   Permit to implement callback method for the click on delete button event.  */
    public interface OnDeleteButtonClickListener { void onDeleteButtonClick(); }

    public void setOnViewClickListener(OnViewClickListener onViewClickListener) {
        this.onViewClickListener = onViewClickListener;
    }

    public void setOnEditButtonClickListener(OnEditButtonClickListener onEditButtonClickListener) {
        this.onEditButtonClickListener = onEditButtonClickListener;
    }

    public void setOnDeleteButtonClickListener(OnDeleteButtonClickListener onDeleteButtonClickListener) {
        this.onDeleteButtonClickListener = onDeleteButtonClickListener;
    }
}
