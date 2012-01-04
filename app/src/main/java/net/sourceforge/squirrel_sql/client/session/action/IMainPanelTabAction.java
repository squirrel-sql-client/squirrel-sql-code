package net.sourceforge.squirrel_sql.client.session.action;


import net.sourceforge.squirrel_sql.client.session.mainpanel.IMainPanelTab;

public interface IMainPanelTabAction extends ISessionAction
{
   void setSelectedMainPanelTab(IMainPanelTab selectedMainTab);
}
