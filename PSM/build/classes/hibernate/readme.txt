在struts.xml中加入：
	<action name="OperationControlAction" class="OperationControlAction">
    		<result name="success"></result>
    	</action>


在applicationContext/.xml中加入：
   映射文件：
				<value>hibernate/SecureJobSlip.hbm.xml</value>
				<value>hibernate/SchemeImple.hbm.xml</value>
				<value>hibernate/FireSafety.hbm.xml</value>
				<value>hibernate/TransportSafety.hbm.xml</value>
	
spring管理struts：

	<bean id="OperationControlAction" class="PSM.Action.OperationControlAction">
		<property name="operationControlService">
			<ref local="OperationControlService"/>
		</property>
	</bean>

service：

	<bean id="OperationControlService" class="PSM.Service.OperationControlService">
		<property name="operationControlDAO">
			<ref local="OperationControlDAO"/>
		</property>
	</bean>

DAO注入sessionFactory：

	<bean id="OperationControlDAO" class="PSM.DAO.OperationControlDAO">
		<property name="sessionFactory">
			<ref local="sessionFactory"/>
			</property>
	</bean>


