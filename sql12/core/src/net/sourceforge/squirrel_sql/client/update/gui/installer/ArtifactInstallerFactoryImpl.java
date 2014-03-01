/*
 * Copyright (C) 2008 Rob Manning
 * manningr@users.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package net.sourceforge.squirrel_sql.client.update.gui.installer;

import java.io.FileNotFoundException;

import net.sourceforge.squirrel_sql.client.update.UpdateUtil;
import net.sourceforge.squirrel_sql.client.update.gui.installer.event.InstallStatusListener;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.ChangeListXmlBean;
import net.sourceforge.squirrel_sql.fw.util.FileWrapper;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class ArtifactInstallerFactoryImpl implements ArtifactInstallerFactory, ApplicationContextAware
{
	private final static String ARTIFACT_INSTALLER_ID =
		"net.sourceforge.squirrel_sql.client.update.gui.installer.ArtifactInstaller";

	private UpdateUtil updateUtil;

	/** Spring-injected */
	private ApplicationContext applicationContext = null;

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
	{
		this.applicationContext = applicationContext;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.update.gui.installer.ArtifactInstallerFactory#
	 * create(net.sourceforge.squirrel_sql.client.update.xmlbeans.ChangeListXmlBean, 
	 * net.sourceforge.squirrel_sql.client.update.gui.installer.event.InstallStatusListener)
	 */
	public ArtifactInstaller create(ChangeListXmlBean changeList, InstallStatusListener listener)
		throws FileNotFoundException
	{
		ArtifactInstaller result = (ArtifactInstaller) applicationContext.getBean(ARTIFACT_INSTALLER_ID);
		if (listener != null) {
			result.addListener(listener);
		}
		result.setChangeList(changeList);
		return result;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.update.gui.installer.ArtifactInstallerFactory#
	 * create(net.sourceforge.squirrel_sql.fw.util.FileWrapper,
	 *      net.sourceforge.squirrel_sql.client.update.gui.installer.event.InstallStatusListener)
	 */
	public ArtifactInstaller create(FileWrapper changeList, InstallStatusListener listener)
		throws FileNotFoundException
	{
		ChangeListXmlBean changeListBean = updateUtil.getChangeList(changeList);
		ArtifactInstaller result = create(changeListBean, listener);
		result.setChangeListFile(changeList);
		return result;
	}

	/**
	 * @param updateUtil
	 *           the updateUtil to set
	 */
	public void setUpdateUtil(UpdateUtil updateUtil)
	{
		this.updateUtil = updateUtil;
	}

}
