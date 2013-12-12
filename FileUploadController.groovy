package oflaherty

import org.springframework.dao.DataIntegrityViolationException;
import groovy.xml.MarkupBuilder
import java.io.File;
import oflaherty.xml.BettingOddsXmlParser;

class FileUploadController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    }

    def list(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        [fileUploadInstanceList: FileUpload.list(params), fileUploadInstanceTotal: FileUpload.count()]
    }

    def save() {
        def fileUploadInstance = new FileUpload(params)
        
		if (!fileUploadInstance.save(flush: true)) {
            render(view: "create", model: [fileUploadInstance: fileUploadInstance])
            return
        }

        flash.message = message(code: 'default.created.message', args: [message(code: 'fileUpload.label', default: 'FileUpload'), fileUploadInstance.id])
        redirect(action: "show", id: fileUploadInstance.id)
    }

    def show(Long id) {
        def fileUploadInstance = FileUpload.get(id)
        
		if (!fileUploadInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'fileUpload.label', default: 'FileUpload'), id])
            redirect(action: "list")
            return
        }

        [fileUploadInstance: fileUploadInstance]
    }

    def edit(Long id) {
        def fileUploadInstance = FileUpload.findById(id)
		
        if (!fileUploadInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'fileUpload.label', default: 'FileUpload'), id])
            redirect(action: "list")
            return
        }

        [fileUploadInstance: fileUploadInstance]
    }

    def update(Long id, Long version) {
        def fileUploadInstance = FileUpload.get(id)
        
		if (!fileUploadInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'fileUpload.label', default: 'FileUpload'), id])
            redirect(action: "list")
            return
        }

        if (version != null) {
            if (fileUploadInstance.version > version) {
                fileUploadInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                          [message(code: 'fileUpload.label', default: 'FileUpload')] as Object[],
                          "Another user has updated this FileUpload while you were editing")
                render(view: "edit", model: [fileUploadInstance: fileUploadInstance])
                return
            }
        }

		if(!params.id.is(fileUploadInstance.path)) {
			try {
				new File(fileUploadInstance.path).renameTo(new File(params.path)) 
			} catch(FileNotFoundException e) {
				log.error("Can't rename file, file not found.", e)				
			}
		}
		
        fileUploadInstance.properties = params

        if (!fileUploadInstance.save(flush: true)) {
            render(view: "edit", model: [fileUploadInstance: fileUploadInstance])
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'fileUpload.label', default: 'FileUpload'), fileUploadInstance.id])
        redirect(action: "show", id: fileUploadInstance.id)
    }

    def delete(Long id) {
        def fileUploadInstance = FileUpload.get(id)
        
		if (!fileUploadInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'fileUpload.label', default: 'FileUpload'), id])
            redirect(action: "list")
            return
        }

        try {
            fileUploadInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'fileUpload.label', default: 'FileUpload'), id])
            redirect(action: "list")
        } catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'fileUpload.label', default: 'FileUpload'), id])
            redirect(action: "show", id: id)
        }
    }
	
	
	def upload() {
		
		def f = request.getFile('file')
		
		if(!f.empty) {
			
			def file = new FileUpload(fileName: f.originalFilename, path: grailsApplication.config.uploadFolder + f.originalFilename, active: true, createdDate: new Date())
			file.save()
			
			f.transferTo(new File(file.path))
			
		} else {
			flash.message = "Please select a valid file to upload"
		}
		
		redirect(action: "list")
	}
	
	def processXml(Long id) {
		
		def fileObject = FileUpload.findById(id)
		def oddsParser = new BettingOddsXmlParser()
		def success = oddsParser.processBettingOdds(fileObject.path)

		if(success.is(true)) {
			flash.message = message(code: 'xml.parse.success', args: [message(code: 'fileUpload.label', default: 'FileUpload'), id])
		} else {
			flash.message = message(code: 'xml.parse.failure', args: [message(code: 'fileUpload.label', default: 'FileUpload'), id])
		}
		
		redirect(action: "list")
		
	}
	
}
