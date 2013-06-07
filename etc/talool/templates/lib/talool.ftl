<#assign bgcolor="#ebebeb"> 
<#assign boxColor="#ffffff">
<#assign boxBorderColor="#c9c9c9">
<#assign boxInsetBorderColor="#e5e5e5">
<#assign buttonLabelColor="#2b2b2b">
<#assign buttonBGColor="#efefef">
<#assign grayTextColor="#a1a3aa">
<#assign lightGrayTextColor="#b0b3b9">
<#assign pageWidth=546>
<#assign fontFamily="Helvetica,Arial,Sans-Serif">


<#macro emailContainer>
	<!DOCTYPE html>
	<html>
		<head></head>
		<body style="background-color:${bgcolor};">
			<div style="background-color:${bgcolor}; padding: 10px 20px 50px;" align="center">
				<div width="100%" align="center" style="background-color:${bgcolor}">
					<table bgcolor="${bgcolor}" border="0" cellpadding="0" cellspacing="0" 
						style="border-spacing:0px;max-width:${pageWidth}px">
						<tbody>
							<tr>
								<td style="padding-top:20px;padding-bottom:20px">
									<@box>
										<#nested/>
									</@box>
									<#include "/components/footer.html"/>
								</td>
							</tr>
						</tbody>
					</table>
				</div>
			</div>
		</body>
	</html>
</#macro>



<#macro box>
	<table border="0" cellpadding="0" cellspacing="0" style="max-width:${pageWidth}px">
		<tbody>
			<tr>
				<td colspan="5" height="16" style="vertical-align:top;max-width:${pageWidth}px">
					<img src="https://statici.icloud.com/emailimages/common/box_top.png" width="100%" height="100%" alt="" style="display:block;max-width:${pageWidth - 1}px">
                </td>
			</tr>
            <tr>
            	<td width="3"></td>
                <td width="1" bgcolor="${boxBorderColor}"></td>
                <td width="${pageWidth - 17}" style="padding:0 4px 0 4px" bgcolor="${boxColor}">
                	<table border="0" bgcolor="${boxColor}" cellpadding="0" cellspacing="0" width="${pageWidth - 17}" style="border-spacing:0px;border-collapse:collapse">
                    	<tbody>
                    		<tr>
                            	<td width="1" bgcolor="${boxInsetBorderColor}"></td>
                              	<td bgcolor="${boxColor}" style="padding:20px 25px">
                              		
                                	<#nested/>
                                	
								</td>
								<td width="1" bgcolor="${boxInsetBorderColor}"></td>
							</tr>
						</tbody>
					</table>
				</td>
				<td width="1" bgcolor="${boxBorderColor}" style="margin:auto"></td>
				<td width="4" style="margin:auto"></td>
			</tr>
			<tr>
				<td colspan="5" height="16" style="vertical-align:top;max-width:${pageWidth}px">
					<img src="https://statici.icloud.com/emailimages/common/box_bottom.png" width="100%" height="100%" alt="" style="display:block;max-width:${pageWidth - 1}px">
				</td>
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
						style="background-repeat:repeat-x;width:60px">
						<div align="center" style="color:${buttonLabelColor};font-family:Helvetica;font-size:15px;font-weight:bold;width:60px;text-align:center;padding-bottom:0px">
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
