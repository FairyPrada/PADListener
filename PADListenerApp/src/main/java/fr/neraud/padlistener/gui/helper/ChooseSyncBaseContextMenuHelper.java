package fr.neraud.padlistener.gui.helper;

import android.content.Context;
import android.util.Log;
import android.view.MenuItem;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import fr.neraud.padlistener.helper.DefaultSharedPreferencesHelper;
import fr.neraud.padlistener.model.ChooseSyncModel;
import fr.neraud.padlistener.model.ChooseSyncModelContainer;
import fr.neraud.padlistener.model.SyncedMonsterModel;
import fr.neraud.padlistener.padherder.constant.MonsterPriority;

/**
 * Base Helper to build and manage a context menu when displaying monsters
 * Created by Neraud on 22/06/2014.
 */
public abstract class ChooseSyncBaseContextMenuHelper {

	/**
	 * All fragments from an Activity receive the onContextItemSelected callback.<br/>
	 * We use a generated unique ID to only use the correct helper to handle a callback
	 *
	 * @see http://adanware.blogspot.fr/2012/05/android-oncreatecontextmenu-in-multiple.html
	 */
	private static final AtomicInteger GROUP_ID_GENERATOR = new AtomicInteger();

	private final Context context;
	private final ChooseSyncDataPagerHelper.Mode mode;
	private final ChooseSyncModel result;
	private final int groupId;

	public ChooseSyncBaseContextMenuHelper(Context context, ChooseSyncDataPagerHelper.Mode mode, ChooseSyncModel result) {
		this.context = context;
		this.mode = mode;
		this.result = result;
		groupId = GROUP_ID_GENERATOR.getAndIncrement();
	}

	public boolean contextItemSelected(MenuItem item) {
		Log.d(getClass().getName(), "contextItemSelected : item = " + item);

		if (item.getGroupId() != groupId) {
			return false;
		} else {
			return doContextItemSelected(item);
		}
	}

	protected abstract boolean doContextItemSelected(MenuItem item);

	protected abstract void notifyDataSetChanged();

	protected ChooseSyncDataPagerHelper.Mode getMode() {
		return mode;
	}

	protected int getGroupId() {
		return groupId;
	}

	protected Context getContext() {
		return context;
	}


	protected CharSequence[] buildPriorityList() {
		final CharSequence[] priorities = new CharSequence[MonsterPriority.values().length];

		for (MonsterPriority priority : MonsterPriority.values()) {
			priorities[priority.ordinal()] = getContext().getString(priority.getLabelResId());
		}
		return priorities;
	}

	protected void addMonsterToIgnoreList(int monsterId) {
		Log.d(getClass().getName(), "addMonsterToIgnoreList : monsterId = " + monsterId);
		final DefaultSharedPreferencesHelper helper = new DefaultSharedPreferencesHelper(getContext());
		final Set<Integer> ignoredIds = helper.getMonsterIgnoreList();
		ignoredIds.add(monsterId);
		helper.setMonsterIgnoreList(ignoredIds);

		if(helper.isChooseSyncUseIgnoreListForMonstersCreated()) {
			filterMonsterList(result.getSyncedMonstersToCreate(), monsterId);
		}
		if(helper.isChooseSyncUseIgnoreListForMonstersUpdated()) {
			filterMonsterList(result.getSyncedMonstersToUpdate(), monsterId);
		}
		if(helper.isChooseSyncUseIgnoreListForMonstersDeleted()) {
			filterMonsterList(result.getSyncedMonstersToDelete(), monsterId);
		}
	}

	private void filterMonsterList(List<ChooseSyncModelContainer<SyncedMonsterModel>> monsters, int monsterId) {
		final Iterator<ChooseSyncModelContainer<SyncedMonsterModel>> iter = monsters.iterator();
		while(iter.hasNext()) {
			final ChooseSyncModelContainer<SyncedMonsterModel> item = iter.next();
			if(item.getSyncedModel().getDisplayedMonsterInfo().getIdJP() == monsterId)  {
				iter.remove();
			}
		}
	}
}
