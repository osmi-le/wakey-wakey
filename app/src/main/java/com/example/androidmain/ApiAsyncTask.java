package com.example.androidmain;


import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import androidx.annotation.RequiresApi;
public class ApiAsyncTask extends AsyncTask<Void, Void, Void>{
    private static final String TAG = "";
    private MainActivity mActivity;

    /**
     * Constructor.
     *
     * @param activity MainActivity that spawned this task.
     */
    ApiAsyncTask(MainActivity activity) {
        this.mActivity = activity;
    }

    /**
     * Background task to call Google Calendar API.
     *
     * @param params no parameters needed for this task.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected Void doInBackground(Void... params) {
        try {
            Log.d("help me", "ent");
            mActivity.clearResultsText(); //결과창 초기화
            mActivity.getCalendarData(getCalendarFromApi()); //사용자가 가진 캘린더 정보 가져옴
            mActivity.updateResultsText(getDataFromApi()); // 데이터 파싱 후 출력

        } catch (final GooglePlayServicesAvailabilityIOException availabilityException) {
            mActivity.showGooglePlayServicesAvailabilityErrorDialog(
                    availabilityException.getConnectionStatusCode());

        } catch (UserRecoverableAuthIOException userRecoverableException) {
            mActivity.startActivityForResult(
                    userRecoverableException.getIntent(),
                    MainActivity.REQUEST_AUTHORIZATION);

        } catch (IOException e) {
            mActivity.updateStatus("The following error occurred: " +
                    e.getMessage());
        }
        return null;
    }

    /**
     * 사용자가 가진 모든 캘린더를 가져옵니다
     *
     * @return List of CalendarDatas describing returned calendars.
     * @throws IOException
     */
    private List<CalendarData> getCalendarFromApi() throws IOException {
        String pageToken = null;
        List<CalendarListEntry> calendarItems;
        List<CalendarData> result = new ArrayList<>();
        result.add(new CalendarData("primary", "primary"));
        do {
            CalendarList calendarList = mActivity.mService.calendarList().list().setPageToken(pageToken).execute();
            calendarItems= calendarList.getItems();

            for (CalendarListEntry calendarListEntry : calendarItems) {
                CalendarData calendarData = new CalendarData(calendarListEntry.getId(), calendarListEntry.getSummary());
                result.add(calendarData);

            }
            pageToken = calendarList.getNextPageToken();
        } while (pageToken != null);

        return result;
    }

    /**
     * 사용자가 가진 모든 이벤트 정보를 가져옵니다.
     *
     * @return List of EventData describing returned events.
     * @throws IOException
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private List<EventData> getDataFromApi() throws IOException {
        // List the next 10 events from the primary calendar.
        DateTime now = new DateTime(System.currentTimeMillis()); //현재 시간을 기준으로 함
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE,1);
        DateTime tomorrow = new DateTime(calendar.getTime().getTime());
        String pageToken = null;
        List<String> eventStrings = new ArrayList<>(); //이벤트 정보를 저장할 list
        List<EventData> eventData = new ArrayList<>();


        do {
            Events events;
            int calId = mActivity.calValue;
            Log.d("calId state: ", String.valueOf(calId));
            if(calId == 0)
                events = mActivity.mService.events().list("primary").setTimeMin(now).setTimeMax(tomorrow).execute();
            else
                events = mActivity.mService.events().list(mActivity.calendarData.get(calId).getCalId()).setTimeMin(now).setTimeMax(tomorrow).execute();

            List<Event> items = events.getItems();

            for (Event event : items) {
                DateTime start = event.getStart().getDateTime();
                DateTime end = event.getEnd().getDateTime();

                if (start == null) {
                    // 하루종일 진행되는 이벤트는 시작시간/ 종료시간이 없기 때문에 시작일을 받아옵니다.
                    start = event.getStart().getDate();
                    EventData tevent = new EventData(event.getSummary(), " ", " ", start.toString().substring(9, 10));
                    eventData.add(tevent);
                }
                else if (start!=null) {
                    //이벤트의 시작 시간, 종료 시간을 받아옵니다.
                    Log.d("starttime", start.toString());
                    EventData tevent = new EventData(event.getSummary(), start.toString().substring(11, 16), end.toString().substring(11, 16), start.toString().substring(9, 10));
                    eventData.add(tevent);
                }

            }
            pageToken = events.getNextPageToken();
        } while (pageToken != null);

        eventData.sort((o1, o2) -> (o1.getStarttime().compareTo(o2.getStarttime())));
        eventData.sort((o1, o2) -> (o1.getStartdate().compareTo(o2.getStartdate())));

        return eventData;
    }
}
