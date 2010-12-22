package net.sourceforge.squirrel_sql.plugins.oracle;
/*
 * Copyright (C) 2002-2003 Colin Bell
 * colbell@users.sourceforge.net
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
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IObjectTypes;

/**
 * This class contains the different database object types for oracle.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class ObjectTypes implements IObjectTypes
{
	private DatabaseObjectType _consumerGroupParent;
	private DatabaseObjectType _functionParent;
	private DatabaseObjectType _indexParent;
	private DatabaseObjectType _instanceParent;
	private DatabaseObjectType _lobParent;
	private DatabaseObjectType _packageParent;
	private DatabaseObjectType _sequenceParent;
	private DatabaseObjectType _sessionParent;
	private DatabaseObjectType _triggerParent;
	private DatabaseObjectType _typeParent;
	private DatabaseObjectType _userParent;
	private DatabaseObjectType _constraintParent;

	private DatabaseObjectType _consumerGroup;
	private DatabaseObjectType _instance;
	private DatabaseObjectType _lob;
	private DatabaseObjectType _package;
	private DatabaseObjectType _session;
	private DatabaseObjectType _type;
	private DatabaseObjectType _constraint;

   public ObjectTypes(OraclePluginResources resources)
   {
      _consumerGroupParent = DatabaseObjectType.createNewDatabaseObjectType("Consumer Groups", resources.getIcon(OraclePluginResources.IKeys.CONSUMERGROUPS_IMAGE));
      _functionParent = DatabaseObjectType.createNewDatabaseObjectType("Functions", resources.getIcon(OraclePluginResources.IKeys.FUNCTIONS_IMAGE));
      _indexParent = DatabaseObjectType.createNewDatabaseObjectType("Indexes", resources.getIcon(OraclePluginResources.IKeys.INDEXES_IMAGE));
      _instanceParent = DatabaseObjectType.createNewDatabaseObjectType("Instances", resources.getIcon(OraclePluginResources.IKeys.INSTANCES_IMAGE));
      _lobParent = DatabaseObjectType.createNewDatabaseObjectType("LOBS", resources.getIcon(OraclePluginResources.IKeys.LOBS_IMAGE));
      _packageParent = DatabaseObjectType.createNewDatabaseObjectType("Packages", resources.getIcon(OraclePluginResources.IKeys.PACKAGES_IMAGE));
      _sequenceParent = DatabaseObjectType.createNewDatabaseObjectType("Sequences", resources.getIcon(OraclePluginResources.IKeys.SQUENCES_IMAGE));
      _sessionParent = DatabaseObjectType.createNewDatabaseObjectType("Sessions", resources.getIcon(OraclePluginResources.IKeys.SESSIONS_IMAGE));
      _triggerParent = DatabaseObjectType.createNewDatabaseObjectType("Triggers", resources.getIcon(OraclePluginResources.IKeys.TRIGGERS_IMAGE));
      _typeParent = DatabaseObjectType.createNewDatabaseObjectType("Types", resources.getIcon(OraclePluginResources.IKeys.TYPES_IMAGE));
      _userParent = DatabaseObjectType.createNewDatabaseObjectType("Users", resources.getIcon(OraclePluginResources.IKeys.USERS_IMAGE));
      _constraintParent = DatabaseObjectType.createNewDatabaseObjectType("Constraints", resources.getIcon(OraclePluginResources.IKeys.CONSTRAINTS_IMAGE));

      _consumerGroup = DatabaseObjectType.createNewDatabaseObjectType("Consumer Group", resources.getIcon(OraclePluginResources.IKeys.CONSUMERGROUP_IMAGE));
      _instance = DatabaseObjectType.createNewDatabaseObjectType("Instance", resources.getIcon(OraclePluginResources.IKeys.INSTANCE_IMAGE));
      _lob = DatabaseObjectType.createNewDatabaseObjectType("LOB", resources.getIcon(OraclePluginResources.IKeys.LOB_IMAGE));
      _package = DatabaseObjectType.createNewDatabaseObjectType("Package", resources.getIcon(OraclePluginResources.IKeys.PACKAGE_IMAGE));
      _session = DatabaseObjectType.createNewDatabaseObjectType("Session", resources.getIcon(OraclePluginResources.IKeys.SESSION_IMAGE));
      _type = DatabaseObjectType.createNewDatabaseObjectType("Type", resources.getIcon(OraclePluginResources.IKeys.TYPE_IMAGE));
      _constraint = DatabaseObjectType.createNewDatabaseObjectType("Constraint", resources.getIcon(OraclePluginResources.IKeys.CONSTRAINT_IMAGE));
   }

   /**
	 * @see net.sourceforge.squirrel_sql.fw.sql.IObjectTypes#getConsumerGroupParent()
	 */
   public DatabaseObjectType getConsumerGroupParent()
   {
      return _consumerGroupParent;
   }

   /**
	 * @see net.sourceforge.squirrel_sql.fw.sql.IObjectTypes#getFunctionParent()
	 */
   public DatabaseObjectType getFunctionParent()
   {
      return _functionParent;
   }

   /**
	 * @see net.sourceforge.squirrel_sql.fw.sql.IObjectTypes#getIndexParent()
	 */
   public DatabaseObjectType getIndexParent()
   {
      return _indexParent;
   }

   /**
	 * @see net.sourceforge.squirrel_sql.fw.sql.IObjectTypes#getInstanceParent()
	 */
   public DatabaseObjectType getInstanceParent()
   {
      return _instanceParent;
   }

   /**
	 * @see net.sourceforge.squirrel_sql.fw.sql.IObjectTypes#getLobParent()
	 */
   public DatabaseObjectType getLobParent()
   {
      return _lobParent;
   }

   /**
	 * @see net.sourceforge.squirrel_sql.fw.sql.IObjectTypes#getPackageParent()
	 */
   public DatabaseObjectType getPackageParent()
   {
      return _packageParent;
   }

   /**
	 * @see net.sourceforge.squirrel_sql.fw.sql.IObjectTypes#getSequenceParent()
	 */
   public DatabaseObjectType getSequenceParent()
   {
      return _sequenceParent;
   }

   /**
	 * @see net.sourceforge.squirrel_sql.fw.sql.IObjectTypes#getSessionParent()
	 */
   public DatabaseObjectType getSessionParent()
   {
      return _sessionParent;
   }

   /**
	 * @see net.sourceforge.squirrel_sql.fw.sql.IObjectTypes#getTriggerParent()
	 */
   public DatabaseObjectType getTriggerParent()
   {
      return _triggerParent;
   }

   /**
	 * @see net.sourceforge.squirrel_sql.fw.sql.IObjectTypes#getTypeParent()
	 */
   public DatabaseObjectType getTypeParent()
   {
      return _typeParent;
   }

   /**
	 * @see net.sourceforge.squirrel_sql.fw.sql.IObjectTypes#getUserParent()
	 */
   public DatabaseObjectType getUserParent()
   {
      return _userParent;
   }

   /**
	 * @see net.sourceforge.squirrel_sql.fw.sql.IObjectTypes#getConstraintParent()
	 */
   public DatabaseObjectType getConstraintParent()
   {
      return _constraintParent;
   }

   /**
	 * @see net.sourceforge.squirrel_sql.fw.sql.IObjectTypes#getConsumerGroup()
	 */
   public DatabaseObjectType getConsumerGroup()
   {
      return _consumerGroup;
   }

   /**
	 * @see net.sourceforge.squirrel_sql.fw.sql.IObjectTypes#getInstance()
	 */
   public DatabaseObjectType getInstance()
   {
      return _instance;
   }

   /**
	 * @see net.sourceforge.squirrel_sql.fw.sql.IObjectTypes#getLob()
	 */
   public DatabaseObjectType getLob()
   {
      return _lob;
   }

   /**
	 * @see net.sourceforge.squirrel_sql.fw.sql.IObjectTypes#getPackage()
	 */
   public DatabaseObjectType getPackage()
   {
      return _package;
   }

   /**
	 * @see net.sourceforge.squirrel_sql.fw.sql.IObjectTypes#getSession()
	 */
   public DatabaseObjectType getSession()
   {
      return _session;
   }

   /**
	 * @see net.sourceforge.squirrel_sql.fw.sql.IObjectTypes#getType()
	 */
   public DatabaseObjectType getType()
   {
      return _type;
   }

   /**
	 * @see net.sourceforge.squirrel_sql.fw.sql.IObjectTypes#getConstraint()
	 */
   public DatabaseObjectType getConstraint()
   {
      return _constraint;
   }
}
