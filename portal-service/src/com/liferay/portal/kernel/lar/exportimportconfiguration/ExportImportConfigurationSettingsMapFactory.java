/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.kernel.lar.exportimportconfiguration;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.lar.ExportImportHelperUtil;
import com.liferay.portal.kernel.staging.StagingUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Group;
import com.liferay.portal.service.GroupLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;

import java.io.Serializable;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.portlet.PortletRequest;

/**
 * @author Daniel Kocsis
 * @author Akos Thurzo
 */
public class ExportImportConfigurationSettingsMapFactory {

	public static Map<String, Serializable> buildExportSettingsMap(
		long userId, long plid, long sourceGroupId, String portletId,
		Map<String, String[]> parameterMap, String cmd, Date startDate,
		Date endDate, String fileName) {

		return buildSettingsMap(
			userId, sourceGroupId, plid, 0, 0, portletId, null, null, null,
			parameterMap, StringPool.BLANK, 0, StringPool.BLANK, null, 0, null,
			cmd, startDate, endDate, null, null, fileName);
	}

	public static Map<String, Serializable> buildImportSettingsMap(
		long userId, long targetGroupId, boolean privateLayout,
		long[] layoutIds, Map<String, String[]> parameterMap, String cmd,
		Date startDate, Date endDate, String fileName) {

		return buildSettingsMap(
			userId, 0, 0, targetGroupId, 0, StringPool.BLANK, privateLayout,
			null, layoutIds, parameterMap, StringPool.BLANK, 0,
			StringPool.BLANK, null, 0, null, cmd, startDate, endDate, null,
			null, fileName);
	}

	public static Map<String, Serializable> buildImportSettingsMap(
		long userId, long plid, long targetGroupId, String portletId,
		Map<String, String[]> parameterMap, String cmd, Date startDate,
		Date endDate, String fileName) {

		return buildSettingsMap(
			userId, 0, plid, targetGroupId, 0, portletId, null, null, null,
			parameterMap, StringPool.BLANK, 0, StringPool.BLANK, null, 0, null,
			cmd, startDate, endDate, null, null, fileName);
	}

	public static Map<String, Serializable> buildSettingsMap(
		long userId, long groupId, boolean privateLayout, long[] layoutIds,
		Map<String, String[]> parameterMap, Date startDate, Date endDate,
		Locale locale, TimeZone timeZone) {

		return buildSettingsMap(
			userId, groupId, 0, privateLayout, layoutIds, parameterMap,
			startDate, endDate, locale, timeZone);
	}

	public static Map<String, Serializable> buildSettingsMap(
		long userId, long sourceGroupId, boolean privateLayout,
		Map<Long, Boolean> layoutIdMap, Map<String, String[]> parameterMap,
		String remoteAddress, int remotePort, String remotePathContext,
		boolean secureConnection, long remoteGroupId,
		boolean remotePrivateLayout, Date startDate, Date endDate,
		Locale locale, TimeZone timeZone) {

		return buildSettingsMap(
			userId, sourceGroupId, 0, 0, 0, StringPool.BLANK, privateLayout,
			layoutIdMap, null, parameterMap, remoteAddress, remotePort,
			remotePathContext, secureConnection, remoteGroupId,
			remotePrivateLayout, StringPool.BLANK, startDate, endDate, locale,
			timeZone, StringPool.BLANK);
	}

	public static Map<String, Serializable> buildSettingsMap(
		long userId, long sourceGroupId, long targetGroupId,
		boolean privateLayout, long[] layoutIds,
		Map<String, String[]> parameterMap, Date startDate, Date endDate,
		Locale locale, TimeZone timeZone) {

		return buildSettingsMap(
			userId, sourceGroupId, 0, targetGroupId, 0, StringPool.BLANK,
			privateLayout, null, layoutIds, parameterMap, StringPool.BLANK, 0,
			StringPool.BLANK, null, 0, null, StringPool.BLANK, startDate,
			endDate, locale, timeZone, StringPool.BLANK);
	}

	public static Map<String, Serializable> buildSettingsMap(
		long userId, long sourceGroupId, long sourcePlid, long targetGroupId,
		long targetPlid, String portletId, Map<String, String[]> parameterMap,
		String cmd, Date startDate, Date endDate) {

		return buildSettingsMap(
			userId, sourceGroupId, sourcePlid, targetGroupId, targetPlid,
			portletId, null, null, null, parameterMap, StringPool.BLANK, 0,
			StringPool.BLANK, null, 0, null, cmd, startDate, endDate, null,
			null, null);
	}

	public static Map<String, Serializable> buildSettingsMap(
			PortletRequest portletRequest, long groupId, int type)
		throws PortalException {

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		boolean privateLayout = ParamUtil.getBoolean(
			portletRequest, "privateLayout");

		Map<Long, Boolean> layoutIdMap = ExportImportHelperUtil.getLayoutIdMap(
			portletRequest);

		if (type == ExportImportConfigurationConstants.TYPE_EXPORT_LAYOUT) {
			long[] layoutIds = ExportImportHelperUtil.getLayoutIds(layoutIdMap);

			return buildSettingsMap(
				themeDisplay.getUserId(), groupId, privateLayout, layoutIds,
				portletRequest.getParameterMap(), null, null,
				themeDisplay.getLocale(), themeDisplay.getTimeZone());
		}

		Group stagingGroup = GroupLocalServiceUtil.getGroup(groupId);

		Group liveGroup = stagingGroup.getLiveGroup();

		Map<String, String[]> parameterMap = StagingUtil.getStagingParameters(
			portletRequest);

		if (liveGroup != null) {
			long[] layoutIds = ExportImportHelperUtil.getLayoutIds(
				layoutIdMap, liveGroup.getGroupId());

			return buildSettingsMap(
				themeDisplay.getUserId(), stagingGroup.getGroupId(),
				liveGroup.getGroupId(), privateLayout, layoutIds, parameterMap,
				null, null, themeDisplay.getLocale(),
				themeDisplay.getTimeZone());
		}

		UnicodeProperties groupTypeSettingsProperties =
			stagingGroup.getTypeSettingsProperties();

		String remoteAddress = ParamUtil.getString(
			portletRequest, "remoteAddress",
			groupTypeSettingsProperties.getProperty("remoteAddress"));

		remoteAddress = StagingUtil.stripProtocolFromRemoteAddress(
			remoteAddress);

		int remotePort = ParamUtil.getInteger(
			portletRequest, "remotePort",
			GetterUtil.getInteger(
				groupTypeSettingsProperties.getProperty("remotePort")));
		String remotePathContext = ParamUtil.getString(
			portletRequest, "remotePathContext",
			groupTypeSettingsProperties.getProperty("remotePathContext"));
		boolean secureConnection = ParamUtil.getBoolean(
			portletRequest, "secureConnection",
			GetterUtil.getBoolean(
				groupTypeSettingsProperties.getProperty("secureConnection")));
		long remoteGroupId = ParamUtil.getLong(
			portletRequest, "remoteGroupId",
			GetterUtil.getLong(
				groupTypeSettingsProperties.getProperty("remoteGroupId")));
		boolean remotePrivateLayout = ParamUtil.getBoolean(
			portletRequest, "remotePrivateLayout");

		StagingUtil.validateRemote(
			groupId, remoteAddress, remotePort, remotePathContext,
			secureConnection, remoteGroupId);

		return buildSettingsMap(
			themeDisplay.getUserId(), groupId, privateLayout, layoutIdMap,
			parameterMap, remoteAddress, remotePort, remotePathContext,
			secureConnection, remoteGroupId, remotePrivateLayout, null, null,
			themeDisplay.getLocale(), themeDisplay.getTimeZone());
	}

	protected static Map<String, Serializable> buildSettingsMap(
		long userId, long sourceGroupId, long sourcePlid, long targetGroupId,
		long targetPlid, String portletId, Boolean privateLayout,
		Map<Long, Boolean> layoutIdMap, long[] layoutIds,
		Map<String, String[]> parameterMap, String remoteAddress,
		int remotePort, String remotePathContext, Boolean secureConnection,
		long remoteGroupId, Boolean remotePrivateLayout, String cmd,
		Date startDate, Date endDate, Locale locale, TimeZone timeZone,
		String fileName) {

		Map<String, Serializable> settingsMap = new HashMap<>();

		if (Validator.isNotNull(cmd)) {
			settingsMap.put(Constants.CMD, cmd);
		}

		if (endDate != null) {
			settingsMap.put("endDate", endDate);
		}

		if (Validator.isNotNull(fileName)) {
			settingsMap.put("fileName", fileName);
		}

		if (MapUtil.isNotEmpty(layoutIdMap)) {
			HashMap<Long, Boolean> serializableLayoutIdMap = new HashMap<>(
				layoutIdMap);

			settingsMap.put("layoutIdMap", serializableLayoutIdMap);
		}

		if (ArrayUtil.isNotEmpty(layoutIds)) {
			settingsMap.put("layoutIds", layoutIds);
		}

		if (locale != null) {
			settingsMap.put("locale", locale);
		}

		if (parameterMap != null) {
			HashMap<String, String[]> serializableParameterMap = new HashMap<>(
				parameterMap);

			settingsMap.put("parameterMap", serializableParameterMap);
		}

		if (Validator.isNotNull(portletId)) {
			settingsMap.put("portletId", portletId);
		}

		if (privateLayout != null) {
			settingsMap.put("privateLayout", privateLayout);
		}

		if (Validator.isNotNull(remoteAddress)) {
			settingsMap.put("remoteAddress", remoteAddress);
		}

		if (remoteGroupId > 0) {
			settingsMap.put("remoteGroupId", remoteGroupId);
		}

		if (Validator.isNotNull(remotePathContext)) {
			settingsMap.put("remotePathContext", remotePathContext);
		}

		if (remotePort > 0) {
			settingsMap.put("remotePort", remotePort);
		}

		if (remotePrivateLayout != null) {
			settingsMap.put("remotePrivateLayout", remotePrivateLayout);
		}

		if (secureConnection != null) {
			settingsMap.put("secureConnection", secureConnection);
		}

		if (sourceGroupId > 0) {
			settingsMap.put("sourceGroupId", sourceGroupId);
		}

		if (startDate != null) {
			settingsMap.put("startDate", startDate);
		}

		if (sourcePlid > 0) {
			settingsMap.put("sourcePlid", sourcePlid);
		}

		if (targetGroupId > 0) {
			settingsMap.put("targetGroupId", targetGroupId);
		}

		if (targetPlid > 0) {
			settingsMap.put("targetPlid", targetPlid);
		}

		if (timeZone != null) {
			settingsMap.put("timezone", timeZone);
		}

		settingsMap.put("userId", userId);

		return settingsMap;
	}

}