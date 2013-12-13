<%@ page import="oflaherty.FileUpload" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'fileUpload.label', default: 'FileUpload')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-fileUpload" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/fileUpload/list')}"><g:message code="default.home.label"/></a></li>
			</ul>
		</div>
		<div id="list-fileUpload" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<h5><g:message code="oflaherty.app.introduction" /></h5>
			<g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
			</g:if>
			
			<g:uploadForm action="upload">
				<fieldset class="form">
                    <input type="file" name="file" />
				</fieldset>
				<fieldset class="buttons">
					<g:submitButton name="upload" class="save" value="Upload" />
				</fieldset>
			</g:uploadForm>
			
			<table>
				<thead>
					<tr>
					
						<g:sortableColumn property="fileName" title="${message(code: 'fileUpload.fileName.label', default: 'File Name')}" />
					
						<g:sortableColumn property="path" title="${message(code: 'fileUpload.path.label', default: 'Path')}" />
					
						<g:sortableColumn property="active" title="${message(code: 'fileUpload.active.label', default: 'Active')}" />
					
						<g:sortableColumn property="createdDate" title="${message(code: 'fileUpload.createdDate.label', default: 'Created Date')}" />
					
						<g:sortableColumn property="xml" title="XML"/>
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${fileUploadInstanceList}" status="i" var="fileUploadInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:link action="show" id="${fileUploadInstance.id}">${fieldValue(bean: fileUploadInstance, field: "fileName")}</g:link></td>
					
						<td>${fieldValue(bean: fileUploadInstance, field: "path")}</td>
					
						<td><g:formatBoolean boolean="${fileUploadInstance.active}" /></td>
					
						<td><g:formatDate date="${fileUploadInstance.createdDate}" /></td>
					
						<td><g:link action="processXml" id="${fileUploadInstance.id}">Process</g:link></td>
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${fileUploadInstanceTotal}" />
			</div>
		</div>
	</body>
</html>
