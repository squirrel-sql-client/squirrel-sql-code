package net.sourceforge.squirrel_sql.client.session.mcp.ui;

public record CallApproval(boolean approved, String userToAiDisapproveResponse)
{
   public static CallApproval approve()
   {
      return new CallApproval(true, null);
   }

   public static CallApproval disapprove(String userToAiDisapproveResponse)
   {
      return new CallApproval(false, userToAiDisapproveResponse);
   }
}
