package com.muei.apm.fasterwho

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment


class EulaDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val mArgs = arguments
            val type = mArgs!!.getString("type")
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            builder.setMessage("Este acuerdo de licencia de usuario final o \"End-User License Agreement\" (\"EULA\") es un acuerdo legal entre usted y Marshmellos (Miriam Breijo Fachal, Ignacio García Sánchez, Christian Manuel Varela Álvarez y María Ocampo Quintáns).\n" +
                    " \n" +
                    "Este acuerdo de EULA rige la adquisición y uso de nuestro software (FasterWho?) directamente de Marshmellos o indirectamente a través de un revendedor o distribuidor autorizado de Marshmellos.\n" +
                    " \n" +
                    "Lea este acuerdo de EULA detenidamente antes de completar el proceso de instalación y utilizar la aplicación FasterWho?. Proporciona una licencia para utilizar esta aplicación y contiene información de garantía y exenciones de responsabilidad.\n" +
                    " \n" +
                    "Si está celebrando este acuerdo EULA en nombre de una empresa u otra entidad legal, declara que tiene la autoridad para obligar a dicha entidad y sus afiliadas a estos términos y condiciones. Si no tiene dicha autoridad o si no está de acuerdo con los términos y condiciones de este contrato de EULA, no instale ni utilice el software y no debe aceptar este contrato de EULA.\n" +
                    " \n" +
                    "Este acuerdo de EULA se aplicará únicamente al software suministrado por Marshmellos en el presente, independientemente de si se hace referencia a otro software o se describe en este documento. Los términos también se aplican a las actualizaciones, suplementos, servicios basados en Internet y servicios de soporte de Marshmellos para el aoftware, a menos que otros términos acompañen a esos elementos en la entrega. Si es así, se aplican esas condiciones.\n" +
                    " \n" +
                    "Por la presente, Marshmellos le otorga una licencia personal, intransferible y no exclusiva para usar la aplicación FasterWho? en sus dispositivos de acuerdo con los términos de este acuerdo EULA.\n" +
                    " \n" +
                    "Se le permite descargar la aplicación FasterWho? para móviles bajo su control. Usted es responsable de garantizar que su dispositivo cumpla con los requisitos mínimos de esta aplicación.\n" +
                    " \n" +
                    "No se le permite:\n" +
                    "    - Editar, alterar, modificar, adaptar, traducir o cambiar la totalidad o parte del software ni permitir que la totalidad o parte del software se combine o se incorpore a cualquier otro software, ni descompilar, desensamblar o realizar ingeniería inversa sobre el software o intentar hacer tales cosas.\n" +
                    "    - Reproducir, copiar, distribuir, revender o utilizar el software para cualquier propósito comercial.\n" +
                    "    - Permitir que un tercero utilice el software en nombre o en beneficio de un tercero.\n" +
                    "    - Utilizar el software de cualquier forma que infrinja las leyes locales, nacionales o internacionales aplicables.\n" +
                    "    - Usar el software para cualquier propósito que Marshmellos considere un incumplimiento de este acuerdo EULA.\n" +
                    "\n" +
                    "Propiedad intelectual y propiedad\n" +
                    " \n" +
                    "Marshmellos conservará en todo momento la propiedad del software tal como lo descargó usted originalmente y todas las descargas posteriores que usted haga. El software (y los derechos de autor y otros derechos de propiedad intelectual de cualquier naturaleza en el software, incluidas las modificaciones realizadas al mismo) son y seguirán siendo propiedad de Marshmellos.\n" +
                    " \n" +
                    "Marshmellos se reserva el derecho de otorgar licencias para usar el software a terceros.\n" +
                    " \n" +
                    "Terminación\n" +
                    " \n" +
                    "Este acuerdo EULA entra en vigencia a partir de la fecha en que utiliza por primera vez el software y continuará hasta que se rescinda. Puede rescindirlo en cualquier momento mediante notificación por escrito a Marshmellos (dirección de email: fasterwhoapm@gmail.com).\n" +
                    " \n" +
                    "También terminará inmediatamente si no cumple con cualquier término de este acuerdo de EULA. Tras dicha terminación, las licencias otorgadas por este acuerdo de EULA terminarán inmediatamente y usted acepta detener todo acceso y uso del software. Las disposiciones que, por su naturaleza, continúan y sobreviven, sobrevivirán a la terminación de este acuerdo de EULA.\n" +
                    " \n" +
                    "Ley regente\n" +
                    " \n" +
                    "Este acuerdo de EULA, y cualquier disputa que surja de o en conexión con este acuerdo de EULA, se regirá e interpretará de acuerdo con las leyes de España.")
                    .setTitle("Aceptar términos y condiciones del EULA")
                    .setPositiveButton(R.string.accept_button,
                            DialogInterface.OnClickListener { dialog, id ->
                                Toast.makeText( activity, "Se acepta el EULA y se procede al registro", Toast.LENGTH_SHORT).show()
                                var intent: Intent? = null
                                if (type.equals("Register")) {
                                    intent = Intent(activity, RegisterActivity::class.java)
                                } else if (type.equals("Google")) {
                                    intent = Intent(activity, GoogleSignInActivity::class.java)
                                }
                                startActivity(intent)
                            })
                    .setNegativeButton(R.string.cancel_button,
                            DialogInterface.OnClickListener { dialog, id ->
                                Toast.makeText( activity, "No se acepta el EULA", Toast.LENGTH_SHORT).show()
                                val intent = Intent(activity, MainActivity::class.java)
                                startActivity(intent)
                            })
            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

}
