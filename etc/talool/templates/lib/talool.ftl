<#assign bgcolor="#ebebeb"> 
<#assign boxColor="#ffffff">
<#assign boxBorderColor="#19bcb9">
<#assign boxInsetBorderColor="#ffffff">
<#assign buttonLabelColor="#2b2b2b">
<#assign buttonBGColor="#19bcb9">
<#assign grayTextColor="#a1a3aa">
<#assign lightGrayTextColor="#b0b3b9">
<#assign fontFamily="Helvetica,Arial,Sans-Serif">

<#assign aboutUs="Talool is committed to bringing you better deals and closer relationships with the places where you shop, eat, relax, and visit.">


<#macro emailContainer title cobrand="" max=700 min=400>
	<!DOCTYPE html>
	<html>
		<head></head>
		<body>
			<style>
				@media screen and (max-width: 480px) {
				   table.table { width: ${min}px !important; }
				}
			</style>
			<div style="background-color:${bgcolor}; padding: 10px 10px 50px; max-width: ${max}px">
			
				<table class="table" border="0" cellpadding="0" cellspacing="0" width="100%">
					<tbody>
						<tr>
							<td width="300">
								<a href="http://talool.com"><img src="http://www.talool.com/img/logo/TaloolLogoAndIcon_brown_xsmall.png" 
									width="120" height="50" border="0" style="display:block" alt="Talool"></a>
							</td>
							<td style="text-align: right">
								<@logo cobrand="${cobrand}"/>
							</td>
						</tr>
					</tbody>
				</table>
			    

				<@box title="${title}">
					<#nested/>
				</@box>
			
				<div align="center">
					<#include "/components/footer.html"/>
				</div>
				
			</div>
		</body>
	</html>
</#macro>

<#macro userEmailContainer>
	<!DOCTYPE html>
	<html>
		<head></head>
		<body><#nested/></body>
	</html>
</#macro>

<#macro box title>
	<table class="table" border="0" cellpadding="0" cellspacing="0" width="100%">
		<tbody>
			<tr>
				<td colspan="3" 
					style="vertical-align:top; color:#fff; font-size:21px; font-weight: bold; padding: 15px 25px 10px" 
					bgcolor="${boxBorderColor}">${title}</td>
			</tr>
            <tr>
            	<td width="1" bgcolor="${boxBorderColor}"></td>
                <td style="padding:25px 25px 50px 25px" bgcolor="${boxColor}">         		
                	<#nested/>
				</td>
				<td width="1" bgcolor="${boxBorderColor}"></td>
			</tr>
			<tr>
				<td colspan="3" height="1" bgcolor="${boxBorderColor}"></td>
			</tr>
		</tbody>
	</table>
</#macro>

<#macro button url label>
	<a href="${url}" style="float:left;color:#000000;outline:none;text-decoration:none" target="_blank">
		<table height="50" cellpadding="0" cellspacing="0">
			<tbody>
				<tr>
					<td height="50" width="40" 
						background="https://statici.icloud.com/emailimages/calweb/email_assets/buttons/accept_btn_left.png" 
						style="background-repeat:no-repeat">
					</td>
					<td height="50" align="center" bgcolor="${buttonBGColor}" 
						background="https://statici.icloud.com/emailimages/calweb/email_assets/buttons/btn_middle.png" 
						style="background-repeat:repeat-x;width:110px">
						<div align="center" style="color:${buttonLabelColor};font-family:Helvetica;font-size:15px;font-weight:bold;width:110px;text-align:center;padding-bottom:0px">
							<a href="${url}" style="color:${buttonLabelColor};outline:none;text-decoration:none" target="_blank"> 
								${label}
							</a>
						</div>
					</td>
					<td height="50" width="32" 
						background="https://statici.icloud.com/emailimages/calweb/email_assets/buttons/invitation_btn_right.png" 
						style="background-repeat:no-repeat">
					</td>
				</tr>
			</tbody>
		</table>
	</a>
</#macro>

<#macro logo cobrand>
	<#switch cobrand>
	  <#case "colorado">
	    <img src="http://static.talool.com/17907b38-9ce4-4fcd-afff-85ac009b2117/130814104647LogoPaybackBoulder.png" 
			border="0" alt="Payback Book"/>
	    <#break>
	  <#case "washington">
	    <img src="http://static.talool.com/17907b38-9ce4-4fcd-afff-85ac009b2117/130804121807LogoPaybackBookVancouver.png" 
			border="0" alt="Payback Book"/>
	    <#break>
          <#case "oregon">
	    <img src="http://static.talool.com/17907b38-9ce4-4fcd-afff-85ac009b2117/140810195239_-1020647579.png" 
			border="0" alt="Payback Mobile"/>
	    <#break>
	  <#default>
	</#switch>
</#macro>
