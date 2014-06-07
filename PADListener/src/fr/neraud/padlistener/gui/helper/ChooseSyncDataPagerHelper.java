
package fr.neraud.padlistener.gui.helper;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import fr.neraud.padlistener.R;
import fr.neraud.padlistener.gui.fragment.ChooseSyncInfoFragment;
import fr.neraud.padlistener.gui.fragment.ChooseSyncMaterialsFragment;
import fr.neraud.padlistener.gui.fragment.ChooseSyncMonstersGroupedFragment;
import fr.neraud.padlistener.gui.fragment.ChooseSyncMonstersSimpleFragment;
import fr.neraud.padlistener.helper.DefaultSharedPreferencesHelper;
import fr.neraud.padlistener.model.ChooseSyncModel;

/**
 * Helper for the ChooseSync
 * 
 * @author Neraud
 */
public class ChooseSyncDataPagerHelper {

	public static final String ARGUMENT_SYNC_RESULT_NAME = "result";
	public static final String ARGUMENT_SYNC_MODE_NAME = "mode";
	public static final String ARGUMENT_ACCOUNT_ID_NAME = "accountId";

	public static enum Mode {
		UPDATED,
		CREATED,
		DELETED;
	}

	private final ChooseSyncModel result;
	private int count = 0;
	private final int accountId;
	private final Map<Integer, Fragment> fragmentsByPosition;
	private final Map<Integer, Integer> titlesByPosition;
	private final DefaultSharedPreferencesHelper prefHelper;

	@SuppressLint("UseSparseArrays")
	public ChooseSyncDataPagerHelper(Context context, int accountId, ChooseSyncModel result) {
		this.result = result;
		this.accountId = accountId;
		fragmentsByPosition = new HashMap<Integer, Fragment>();
		titlesByPosition = new HashMap<Integer, Integer>();
		prefHelper = new DefaultSharedPreferencesHelper(context);

		populate();
	}

	private void populate() {
		Log.d(getClass().getName(), "populate");
		populateInfoFragment();
		populateInfoMaterialsUpdated();
		populateInfoMonstersUpdated();
		populateInfoMonstersCreated();
		populateInfoMonstersDeleted();
	}

	private void populateInfoFragment() {
		Log.d(getClass().getName(), "populateInfoFragment");
		final ChooseSyncInfoFragment fragment = new ChooseSyncInfoFragment();
		addResultToFragmentArguments(fragment);
		fragment.getArguments().putInt(ARGUMENT_ACCOUNT_ID_NAME, accountId);
		fragmentsByPosition.put(count, fragment);
		titlesByPosition.put(count, R.string.choose_sync_info_label);
		count++;
	}

	private void populateInfoMaterialsUpdated() {
		Log.d(getClass().getName(), "populateInfoMaterialsUpdated");
		if (result.getSyncedMaterialsToUpdate().size() > 0) {
			final Fragment fragment = new ChooseSyncMaterialsFragment();
			addResultToFragmentArguments(fragment);
			fragmentsByPosition.put(count, fragment);
			titlesByPosition.put(count, R.string.choose_sync_materialsUpdated_label);
			count++;
		}
	}

	private void populateInfoMonstersUpdated() {
		Log.d(getClass().getName(), "populateInfoMonstersUpdated");
		if (result.getSyncedMonstersToUpdate().size() > 0) {
			final Fragment fragment = prefHelper.isChooseSyncGroupMonstersUpdated() ? new ChooseSyncMonstersGroupedFragment()
			        : new ChooseSyncMonstersSimpleFragment();
			addResultToFragmentArguments(fragment);
			addModeToFragmentArguments(fragment, Mode.UPDATED);
			fragmentsByPosition.put(count, fragment);
			titlesByPosition.put(count, R.string.choose_sync_monstersUpdated_label);
			count++;
		}
	}

	private void populateInfoMonstersCreated() {
		Log.d(getClass().getName(), "populateInfoMonstersCreated");
		if (result.getSyncedMonstersToCreate().size() > 0) {
			final Fragment fragment = prefHelper.isChooseSyncGroupMonstersCreated() ? new ChooseSyncMonstersGroupedFragment()
			        : new ChooseSyncMonstersSimpleFragment();
			addResultToFragmentArguments(fragment);
			addModeToFragmentArguments(fragment, Mode.CREATED);
			fragmentsByPosition.put(count, fragment);
			titlesByPosition.put(count, R.string.choose_sync_monstersCreated_label);
			count++;
		}
	}

	private void populateInfoMonstersDeleted() {
		Log.d(getClass().getName(), "populateInfoMonstersDeleted");
		if (result.getSyncedMonstersToDelete().size() > 0) {
			final Fragment fragment = prefHelper.isChooseSyncGroupMonstersDeleted() ? new ChooseSyncMonstersGroupedFragment()
			        : new ChooseSyncMonstersSimpleFragment();
			addResultToFragmentArguments(fragment);
			addModeToFragmentArguments(fragment, Mode.DELETED);
			fragmentsByPosition.put(count, fragment);
			titlesByPosition.put(count, R.string.choose_sync_monstersDeleted_label);
			count++;
		}
	}

	private void addResultToFragmentArguments(Fragment fragment) {
		if (fragment.getArguments() == null) {
			fragment.setArguments(new Bundle());
		}
		fragment.getArguments().putSerializable(ARGUMENT_SYNC_RESULT_NAME, result);
	}

	private void addModeToFragmentArguments(Fragment fragment, Mode mode) {
		if (fragment.getArguments() == null) {
			fragment.setArguments(new Bundle());
		}
		fragment.getArguments().putSerializable(ARGUMENT_SYNC_MODE_NAME, mode.name());
	}

	public int getCount() {
		return count;
	}

	public Fragment createFragment(int position) {
		return fragmentsByPosition.get(position);
	}

	public Integer getTitle(int position) {
		return titlesByPosition.get(position);
	}

}
