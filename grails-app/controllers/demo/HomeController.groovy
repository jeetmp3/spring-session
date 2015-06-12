package demo

class HomeController {

    def index() {
        println grailsApplication.mainContext.getBean("springSessionRepositoryFilter")
        render "HELLO"
    }

    def save(String name, Object value){
        session.setAttribute(name, value)
        render "saved ${name}->${value}"
    }

    def list(){
        render session.id
        session.attributeNames.each {
            render it+" -> "+session.getAttribute(it)
        }
    }

    def create() {
        println session.id
        render session.id
    }

    def expire() {
        println "Expiring session"
        session.invalidate()
        render "Done"
    }
}
