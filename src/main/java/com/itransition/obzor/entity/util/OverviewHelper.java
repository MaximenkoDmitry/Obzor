package com.itransition.obzor.entity.util;

import com.itransition.obzor.entity.User;

public abstract class OverviewHelper {
	public static String getCreatorName(User user) {
		return user != null ? user.getUsername() : "<none>";
	}
}
