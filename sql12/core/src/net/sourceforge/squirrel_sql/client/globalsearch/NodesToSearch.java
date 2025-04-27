package net.sourceforge.squirrel_sql.client.globalsearch;

import java.util.List;

public record NodesToSearch(List<GlobSearchNodeSession> globSearchNodeSessions, List<GlobSearchNodeCellDataDialog> globSearchNodeCellDataDialogs)
{
}
