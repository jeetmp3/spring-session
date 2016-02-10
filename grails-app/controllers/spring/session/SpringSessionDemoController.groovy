package spring.session

class SpringSessionDemoController {

    def set(String key, String value) {
        session[key] = value
        println session.id
        render "success"
    }

    def get(String key) {
        println session.id
        render(session[key] ?: "NO-KEY")
    }

    def setMutable(String name) {
        session.demoMap = [name: name]
        render "done"
    }

    def updateMutable(String name) {
        Map demoMap = session.demoMap
        demoMap.name = name
        render "ok"
    }

    def getFinalValue(String name) {
        Map sessionMap = session.demoMap
        render(sessionMap[name])
    }
}
