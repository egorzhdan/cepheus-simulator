import build.BuildSystem
import kotlinx.html.*
import kotlinx.html.stream.createHTML
import org.jetbrains.ktor.util.unescapeIfQuoted

/**
 * @author Egor Zhdan
 */
object Frontend {

    fun makeIndexHTML() = createHTML().html {
        makeHead { }

        makeBody {
            br()
            p("lead") {
                +"This is just a template, go on"
                br()
                a("/simulator") {
                    +"Brand-new revolutionary simulator"
                }
            }
        }
    }

    fun makeSimulatorHTML() = createHTML().html {
        makeHead {
            style {
                //language=CSS
                +" td { padding: 0 !important; }"
                //language=CSS
                +" h3 { margin-bottom: 1rem; }"

                //language=CSS
                +" #run { margin-bottom: 1rem; }"
                //language=CSS
                +" .log-row { margin-bottom: 0; }"
                //language=CSS
                +" .card-row { margin-bottom: 1rem; margin-top: 2rem; }"
                //language=CSS
                +" .desc-span { font-size: 0.7rem; display: inline-block; text-align: center; width: 60%; margin: 0 !important; }"

                //language=CSS
                +" .btn-up { padding: 0; height: 0.6rem; width: 100%; border-radius: 0.5rem 0.5rem 0 0; }"
                //language=CSS
                +" .btn-down { padding: 0; height: 0.6rem; width: 100%; border-radius: 0 0 0.5rem 0.5rem; }"
                //language=CSS
                +" .btn-left { padding: 0; height: 1.5rem; width: 20%; border-radius: 0; }"
                //language=CSS
                +" .btn-right { padding: 0; height: 1.5rem; width: 20%; border-radius: 0; }"
            }
        }

        makeBody(fluid = true) {
            row {
                col(9) {
                    row {
                        col(8) {
                            h3("") { +"Field" }
                        }
                        col(2) {
                            button(classes = "btn btn-secondary btn-sm full-width") {
                                attributes["id"] = "open"
                                attributes["onclick"] = "$('#open-input').trigger('click');"

                                +"Open"
                            }
                            input(InputType.file) {
                                attributes["id"] = "open-input"
                                attributes["style"] = "display: none;"
                                attributes["onchange"] = "openField(this)"
                            }
                        }
                        col(2) {
                            button(classes = "btn btn-secondary btn-sm full-width") {
                                attributes["id"] = "save"

                                +"Save"
                            }
                        }
                    }

                    table("table table-bordered") {
                        tbody {
                            (0 until 16).forEach { row ->
                                tr("field-tr") {
                                    attributes["id"] = "field-$row"

                                    (0 until 16).forEach { col ->
                                        td("field-td") {
                                            attributes["id"] = "field-$row-$col"

                                            button(classes = "btn btn-secondary btn-up") {
                                                attributes["onclick"] = "toggleCell($row, $col, 'top', this)"
                                            }
                                            button(classes = "btn btn-secondary btn-left") {
                                                attributes["onclick"] = "toggleCell($row, $col, 'left', this)"
                                            }
                                            span("desc-span") {
                                                attributes["id"] = "desc-$row-$col"

                                                +"↓"
                                            }
                                            button(classes = "btn btn-secondary btn-right float-right") {
                                                attributes["onclick"] = "toggleCell($row, $col, 'right', this)"
                                            }
                                            button(classes = "btn btn-secondary btn-down") {
                                                attributes["onclick"] = "toggleCell($row, $col, 'bottom', this)"
                                            }

                                        }
                                    }
                                }
                            }
                        }
                    }

                    row("card-row") {
                        col(5) {
                            card(title = "Templates", text = "Download pre-configured templates",
                                    links = arrayOf("/static/simulator-template.py" to "Python"))
                        }
                        col(7) {
                            card(title = "Custom", links = emptyArray()) {
                                +"Print following commands to stdout:"
                                ul {
                                    li { code { +"/robot move forward" } }
                                    li { code { +"/robot turn left" } }
                                    li { code { +"/robot turn right" } }
                                    li { +"anything not starting with slash is debug output" }
                                }
                            }
                        }
                    }
                }

                col(3) {
                    row {
                        col(10) {
                            h3("") { +"Code" }
                        }
                    }

                    row {
                        attributes["style"] = "margin-bottom: 1rem"

                        col(12) {
                            select("custom-select full-width") {
                                attributes["id"] = "compiler"

                                BuildSystem.all.forEach {
                                    option { +it.name }
                                }
                            }
                            label("custom-file full-width") {
                                attributes["style"] = "margin-top: 0.5rem"

                                input(InputType.file, classes = "custom-file-input") {
                                    attributes["id"] = "file"
                                    attributes["onchange"] = "readFile(event)"
                                }
                                span("custom-file-control")
                            }
                        }
                    }

                    button(classes = "btn btn-primary full-width") {
                        attributes["id"] = "run"
                        attributes["onclick"] = "run()"

                        +"Run"
                    }

                    samp {
                        attributes["id"] = "log"
                    }
                }
            }

            script(src = "/static/simulator.js")
        }
    }

}

fun HTML.makeHead(block: HEAD.() -> Unit) {
    head {
        title("Cepheus")
        script(src = "https://cdnjs.cloudflare.com/ajax/libs/jquery/3.2.1/jquery.min.js")
        styleLink("https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-beta.2/css/bootstrap.min.css")
        script(src = "https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.3/umd/popper.min.js")
        script(src = "https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-beta.2/js/bootstrap.min.js")
        style {
            //language=CSS
            +".navbar { margin-top: 1rem; margin-bottom: 1rem; }"
            //language=CSS
            +".full-width { width: 100%; }"
        }
        block()
    }
}

fun HTML.makeBody(fluid: Boolean = false, block: DIV.() -> Unit) {
    body {
        container(fluid) {
            makeNavBar { }
        }

        container(fluid) {
            block()
        }
    }
}

fun DIV.makeNavBar(block: NAV.() -> Unit) {
    nav("navbar navbar-dark bg-dark rounded") {
        a("/", classes = "navbar-brand") { +"Cepheus" }

        block()
    }
}

fun BODY.container(fluid: Boolean = false, block: DIV.() -> Unit) {
    div(if (fluid) "container-fluid" else "container") {
        block()
    }
}

fun DIV.col(width: Int? = null, block: DIV.() -> Unit) {
    div("col" + if (width != null) "-" + width else "") {
        block()
    }
}

fun DIV.row(classes: String? = null, block: DIV.() -> Unit) {
    div("row" + if (classes != null) " " + classes else "") {
        block()
    }
}

fun DIV.card(title: String, subtitle: String? = null, text: String? = null, links: Array<Pair<String, String>>, block: P.() -> Unit = { }) {
    div("card") {
        div("card-body") {
            h4("card-title") { +title }
            if (subtitle != null) h6("card-subtitle mb-2 text-muted") { +subtitle }
            p("card-text") {
                if (text != null) +text
                block()
            }

            links.forEach {
                a(it.first, classes = "card-link") { +it.second }
            }
        }
    }
}
