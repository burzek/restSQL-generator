<?xml version='1.0' encoding='UTF-8'?>
<rs:sqlResource xmlns:rs="http://restsql.org/schema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://restsql.org/schema SqlResource.xsd ">
	<query>
    	%%QUERY%%
	</query>   
	<metadata>
		<database default="%%DB%%"/>
		<table name="%%TABLE%%" role="Parent"/>
	</metadata>
</rs:sqlResource>