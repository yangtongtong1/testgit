��struts.xml�м��룺
	<action name="OperationControlAction" class="OperationControlAction">
    		<result name="success"></result>
    	</action>


��applicationContext/.xml�м��룺
   ӳ���ļ���
				<value>hibernate/SecureJobSlip.hbm.xml</value>
				<value>hibernate/SchemeImple.hbm.xml</value>
				<value>hibernate/FireSafety.hbm.xml</value>
				<value>hibernate/TransportSafety.hbm.xml</value>
	
spring����struts��

	<bean id="OperationControlAction" class="PSM.Action.OperationControlAction">
		<property name="operationControlService">
			<ref local="OperationControlService"/>
		</property>
	</bean>

service��

	<bean id="OperationControlService" class="PSM.Service.OperationControlService">
		<property name="operationControlDAO">
			<ref local="OperationControlDAO"/>
		</property>
	</bean>

DAOע��sessionFactory��

	<bean id="OperationControlDAO" class="PSM.DAO.OperationControlDAO">
		<property name="sessionFactory">
			<ref local="sessionFactory"/>
			</property>
	</bean>


