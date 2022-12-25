package hu.montlikadani.tablist.user;

import hu.montlikadani.tablist.TabList;
import hu.montlikadani.tablist.config.constantsLoader.ConfigValues;
import hu.montlikadani.tablist.tablist.TabHandler;
import hu.montlikadani.tablist.tablist.groups.GroupPlayer;
import hu.montlikadani.tablist.tablist.playerlist.HidePlayers;
import hu.montlikadani.tablist.tablist.playerlist.PlayerList;
import hu.montlikadani.tablist.utils.ServerVersion;

import java.util.UUID;

import org.bukkit.entity.Player;

public class TabListPlayer implements TabListUser {

	private final TabList plugin;
	private final UUID uniqueId;

	private transient final GroupPlayer groupPlayer;
	private transient final TabHandler tabHandler;
	private transient final PlayerScore playerScore;

	private transient HidePlayers hidePlayers;
	private transient PlayerList playerList;

	private boolean tabVisible = true;

	public TabListPlayer(TabList plugin, UUID uniqueId) {
		this.plugin = plugin;
		this.uniqueId = uniqueId;

		String scoreName = "";

		if (ConfigValues.getObjectType() != hu.montlikadani.tablist.Objects.ObjectTypes.NONE) {
			String entry = plugin.getServer().getOfflinePlayer(uniqueId).getName();

			if (entry != null) {
				if (ServerVersion.isCurrentLower(ServerVersion.v1_18_R1)) {
					if (entry.length() > 40) {
						entry = entry.substring(0, 40);
					}
				} else if (entry.length() > Short.MAX_VALUE) {
					entry = entry.substring(0, Short.MAX_VALUE);
				}

				scoreName = entry;
			}
		}

		playerScore = new PlayerScore(scoreName);
		tabHandler = new TabHandler(plugin, this);
		groupPlayer = new GroupPlayer(plugin, this);
	}

	@Override
	public Player getPlayer() {
		return plugin.getServer().getPlayer(uniqueId);
	}

	@Override
	public UUID getUniqueId() {
		return uniqueId;
	}

	@Override
	public GroupPlayer getGroupPlayer() {
		return groupPlayer;
	}

	@Override
	public TabHandler getTabHandler() {
		return tabHandler;
	}

	@Override
	public boolean isHidden() {
		return playerList != null;
	}

	@Override
	public void setHidden(boolean hidden) {
		if (hidden) {
			if (playerList == null) {
				playerList = new PlayerList(plugin, this);
			}

			playerList.hide();
		} else if (playerList != null) {
			playerList.showEveryone();
			playerList = null;
		}
	}

	@Override
	public boolean isRemovedFromPlayerList() {
		return hidePlayers != null;
	}

	@Override
	public void removeFromPlayerList() {
		if (hidePlayers == null) {
			hidePlayers = new HidePlayers(uniqueId);
			hidePlayers.removePlayerFromTab();
		}
	}

	@Override
	public void addToPlayerList() {
		if (hidePlayers != null) {
			hidePlayers.addPlayerToTab();
			hidePlayers = null;
		}
	}

	@Override
	public boolean equals(Object o) {
		return o != null && o.getClass() == getClass() && uniqueId.equals(((TabListPlayer) o).uniqueId);
	}

	public PlayerList getPlayerList() {
		return playerList;
	}

	@Override
	public PlayerScore getPlayerScore() {
		return playerScore;
	}

	@Override
	public boolean isTabVisible() {
		return tabVisible;
	}

	@Override
	public void setTabVisibility(boolean visibility) {
		tabVisible = visibility;
	}
}
