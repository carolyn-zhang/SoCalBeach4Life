package com.example.socalbeach4life;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.example.socalbeach4life.fragments.RestaurantsFragment;
import com.example.socalbeach4life.fragments.TripsFragment;
import com.google.api.Distribution;
import com.google.firebase.database.DatabaseReference;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(TripsFragment.class)
public class TripsFragmentUnitTests {

    @Test
    public void testOnCreateView() throws Exception {
        TripsFragment tripsFragment = new TripsFragment();
        LayoutInflater mockLayoutInflater = mock(LayoutInflater.class);
        ViewGroup mockViewGroup = mock(ViewGroup.class);
        Bundle mockBundle = mock(Bundle.class);
        LinearLayout mockLinearLayout = mock(LinearLayout.class);
        PowerMockito.whenNew(LinearLayout.class).withAnyArguments().thenReturn(mockLinearLayout);
        DatabaseReference mockDBReference = mock(DatabaseReference.class);
        tripsFragment.databaseReference = mockDBReference;
        PowerMockito.when(mockDBReference.child(anyString())).thenReturn(mock(DatabaseReference.class));
        View mockView = mock(View.class);
        PowerMockito.when(mockLayoutInflater.inflate(anyInt(), any(), anyBoolean())).thenReturn(mockView);
        PowerMockito.when(mockView.findViewById(anyInt())).thenReturn(mock(ScrollView.class));
        PowerMockito.when(mockDBReference.child(anyString())).thenReturn(mockDBReference);

        // when
        tripsFragment.onCreateView(mockLayoutInflater, mockViewGroup, mockBundle);

        // then
        verify(mockDBReference, times(1)).addListenerForSingleValueEvent(any());
    }
}
