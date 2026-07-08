package net.sourceforge.squirrel_sql.client.session.mcp.ui;

/**
 * Allows each MCP-Tab, see {@link McpBarCtrl} and {@link McpServerContext}, its own set of MCP-properties.
 * See also {@link McpUiPropsUtil#createMcpUiPropsInstance()}.
 */
public class McpUiProps
{
   private boolean approveAllAiCalls;
   private boolean applyAliasesReadOnlyRules;
   private boolean allowAccessFormLocalhostOnly;

   public McpUiProps setApproveAllAiCalls(boolean approveAllAiCalls)
   {
      this.approveAllAiCalls = approveAllAiCalls;
      return this;
   }

   public boolean isApproveAllAiCalls()
   {
      return approveAllAiCalls;
   }

   public McpUiProps setApplyAliasesReadOnlyRules(boolean applyAliasesReadOnlyRules)
   {
      this.applyAliasesReadOnlyRules = applyAliasesReadOnlyRules;
      return this;
   }

   public boolean isApplyAliasesReadOnlyRules()
   {
      return applyAliasesReadOnlyRules;
   }

   public McpUiProps setAllowAccessFormLocalhostOnly(boolean allowAccessFormLocalhostOnly)
   {
      this.allowAccessFormLocalhostOnly = allowAccessFormLocalhostOnly;
      return this;
   }

   public boolean isAllowAccessFormLocalhostOnly()
   {
      return allowAccessFormLocalhostOnly;
   }
}
