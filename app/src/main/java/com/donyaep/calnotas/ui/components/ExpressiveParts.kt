package com.donyaep.calnotas.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.toPath
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.graphics.shapes.Morph
import androidx.graphics.shapes.RoundedPolygon
import kotlinx.coroutines.flow.drop
import com.donyaep.calnotas.ui.theme.isAppInDarkTheme
import kotlin.math.PI
import kotlin.math.sin

/** Forma intermedia de un [Morph], escalada y centrada en el tamaño del elemento. */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
class MorphShape(private val morph: Morph, private val progress: Float) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val path = morph.toPath(progress)
        path.transform(Matrix().apply { scale(x = size.width, y = size.height) })
        path.translate(Offset(size.width / 2, size.height / 2) - path.getBounds().center)
        return Outline.Generic(path)
    }

    override fun equals(other: Any?): Boolean =
        other is MorphShape && other.morph === morph && other.progress == progress

    override fun hashCode(): Int = 31 * morph.hashCode() + progress.hashCode()
}

/**
 * Una forma que, cuando cambia [target], se transforma desde la anterior con el resorte del
 * tema en vez de saltar. Así un cambio de estado se ve como el mismo objeto que cambia.
 */
@Composable
fun rememberMorphingShape(target: RoundedPolygon): Shape {
    var from by remember { mutableStateOf(target) }
    var to by remember { mutableStateOf(target) }
    val progress = remember { Animatable(1f) }
    val spec = MaterialTheme.motionScheme.defaultSpatialSpec<Float>()
    LaunchedEffect(target) {
        if (target !== to) {
            from = to
            to = target
            progress.snapTo(0f)
            progress.animateTo(1f, spec)
        }
    }
    val morph = remember(from, to) { Morph(from, to) }
    return MorphShape(morph, progress.value.coerceIn(0f, 1f))
}

// El canto de una forma con volumen: el mismo color, más oscuro. En claro se oscurece menos.
@Composable
fun depthColor(face: Color): Color = lerp(face, Color.Black, if (isAppInDarkTheme()) 0.35f else 0.2f)

fun lerpDp(start: Dp, stop: Dp, fraction: Float): Dp = start + (stop - start) * fraction

/** Dos capas de la misma forma en tonos cercanos al fondo, para dar profundidad sin ruido. */
@Composable
fun LayeredBackgroundShape(
    shape: Shape,
    backSize: Dp,
    frontSize: Dp,
    frontX: Dp,
    frontY: Dp,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    // Mezcladas con el fondo: los contenedores del color dinámico son más grises que el fondo.
    Box(modifier = modifier.size(backSize)) {
        Box(Modifier.fillMaxSize().background(lerp(colors.background, colors.surfaceContainerLow, 0.55f), shape))
        Box(
            Modifier
                .offset(frontX, frontY)
                .size(frontSize)
                .background(lerp(colors.background, colors.surfaceContainer, 0.6f), shape)
        )
    }
}

/** Esquinas de un bloque dentro de un grupo: anchas por fuera, estrechas entre bloques. */
fun connectedShape(index: Int, count: Int, outer: Dp = 24.dp, inner: Dp = 6.dp): RoundedCornerShape = when {
    count == 1 -> RoundedCornerShape(outer)
    index == 0 -> RoundedCornerShape(topStart = outer, topEnd = outer, bottomStart = inner, bottomEnd = inner)
    index == count - 1 -> RoundedCornerShape(topStart = inner, topEnd = inner, bottomStart = outer, bottomEnd = outer)
    else -> RoundedCornerShape(inner)
}

/**
 * Estado de un campo de texto atado a un valor del ViewModel: lo que se escribe sube por
 * [onValueChange] y lo que el ViewModel corrige (normalizar, vaciar al limpiar) baja al campo.
 * [resyncKey] fuerza a copiar el valor otra vez: cuando el ViewModel rechaza lo escrito en un
 * campo que ya estaba vacío, el valor no cambia y el campo se quedaría con el texto rechazado.
 */
@Composable
fun rememberSyncedTextFieldState(value: String, onValueChange: (String) -> Unit, resyncKey: Any? = null): TextFieldState {
    val state = rememberTextFieldState(value)
    val latestOnChange by rememberUpdatedState(onValueChange)
    LaunchedEffect(state) {
        snapshotFlow { state.text.toString() }
            .drop(1)
            .collect { latestOnChange(it) }
    }
    LaunchedEffect(value, resyncKey) {
        if (state.text.toString() != value) state.setTextAndPlaceCursorAtEnd(value)
    }
    return state
}

/** Título de pantalla en una línea, sin barra superior: volver vive en la barra flotante de abajo. */
@Composable
fun ScreenTitle(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge.copy(fontSize = 19.sp, letterSpacing = (-0.2).sp),
        fontWeight = FontWeight.SemiBold,
        modifier = modifier
            .padding(horizontal = 24.dp)
            .height(56.dp)
            .padding(top = 16.dp)
    )
}

@Composable
fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium.copy(fontSize = 15.sp),
        fontWeight = FontWeight.SemiBold,
        modifier = modifier
    )
}

/**
 * Estructura de las pantallas secundarias, la misma de las calculadoras: la forma de fondo arriba a
 * la derecha, el contenido desplazable bajo el título y la barra flotante abajo con [toolbar].
 */
@Composable
fun SecondaryScreenScaffold(
    title: String,
    backgroundShape: Shape,
    toolbar: @Composable RowScope.() -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LayeredBackgroundShape(
            backgroundShape, 300.dp, 260.dp, 16.dp, 10.dp,
            Modifier.align(Alignment.TopEnd).offset(120.dp, (-90).dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 112.dp)
        ) {
            ScreenTitle(title)
            content()
        }

        HorizontalFloatingToolbar(
            expanded = true,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 24.dp),
            content = toolbar
        )
    }
}

/** Titular con el subrayado ondulado bajo [highlight], la misma onda de los indicadores M3E. */
@Composable
fun WavyHeadline(
    text: String,
    highlight: String,
    waveColor: Color,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.headlineMedium.copy(
        fontSize = 40.sp,
        lineHeight = 44.sp,
        letterSpacing = (-0.8).sp
    )
) {
    var layout by remember { mutableStateOf<TextLayoutResult?>(null) }
    val start = text.indexOf(highlight)

    Text(
        text = text,
        style = style,
        fontWeight = FontWeight.Bold,
        onTextLayout = { layout = it },
        modifier = modifier.drawBehind {
            val result = layout ?: return@drawBehind
            if (start < 0) return@drawBehind
            val first = result.getBoundingBox(start)
            val last = result.getBoundingBox(start + highlight.length - 1)
            val line = result.getLineForOffset(start)
            val y = result.getLineBaseline(line) + 7.dp.toPx()
            val amplitude = 2.5.dp.toPx()
            val wavelength = 16.dp.toPx()
            val left = minOf(first.left, last.left)
            val right = maxOf(first.right, last.right)
            val path = Path().apply {
                moveTo(left, y)
                var x = left
                while (x <= right) {
                    lineTo(x, y + amplitude * sin(2 * PI * (x - left) / wavelength).toFloat())
                    x += 1f
                }
            }
            drawPath(path, waveColor, style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round))
        }
    )
}

/** El hueco de lo que falta asignar: solo el contorno, para que se lea vacío junto a los tramos llenos. */
@Composable
fun DashedBlock(text: String, modifier: Modifier, shape: RoundedCornerShape) {
    val colors = MaterialTheme.colorScheme
    Box(
        modifier = modifier.border(2.dp, colors.outlineVariant, shape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text,
            style = MaterialTheme.typography.labelLarge.copy(fontSize = 13.sp),
            fontWeight = FontWeight.SemiBold,
            color = colors.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 6.dp)
        )
    }
}

/** Igual que [connectedShape], para tramos en fila: anchas a los extremos, estrechas entre tramos. */
fun connectedRowShape(index: Int, count: Int, outer: Dp = 16.dp, inner: Dp = 4.dp): RoundedCornerShape = when {
    count == 1 -> RoundedCornerShape(outer)
    index == 0 -> RoundedCornerShape(topStart = outer, bottomStart = outer, topEnd = inner, bottomEnd = inner)
    index == count - 1 -> RoundedCornerShape(topStart = inner, bottomStart = inner, topEnd = outer, bottomEnd = outer)
    else -> RoundedCornerShape(inner)
}

/** Pista bajo un campo: una sugerencia, o la nota que por sí sola asegura aprobar. */
enum class HintKind { Suggestion, Secure }

/**
 * Campo numérico grande: la etiqueta arriba, el valor en grande y, si aplica, una pista debajo.
 * [trailingLabel] va a la derecha de la etiqueta (por ejemplo el peso, «15 %»).
 */
@Composable
fun NumberTile(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    trailingLabel: String? = null,
    unit: String? = null,
    hint: Pair<HintKind, String>? = null,
    resyncKey: Any? = null
) {
    val colors = MaterialTheme.colorScheme
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        BasicTextField(
            state = rememberSyncedTextFieldState(value, onValueChange, resyncKey),
            lineLimits = TextFieldLineLimits.SingleLine,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            textStyle = MaterialTheme.typography.headlineMedium.copy(
                fontSize = 26.sp,
                lineHeight = 32.sp,
                fontWeight = FontWeight.SemiBold,
                fontFeatureSettings = "tnum",
                color = colors.onSurface
            ),
            cursorBrush = SolidColor(colors.primary),
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = label },
            decorator = { field ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colors.surfaceContainerHigh, RoundedCornerShape(16.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(label, style = MaterialTheme.typography.labelLarge.copy(fontSize = 12.5.sp), color = colors.onSurfaceVariant)
                        if (trailingLabel != null) {
                            Text(trailingLabel, style = MaterialTheme.typography.labelLarge.copy(fontSize = 12.5.sp), color = colors.outline)
                        }
                    }
                    Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Box(Modifier.weight(1f, fill = false)) {
                            if (value.isEmpty()) {
                                Text(
                                    "—",
                                    style = MaterialTheme.typography.headlineMedium.copy(fontSize = 26.sp, lineHeight = 32.sp),
                                    color = colors.outline
                                )
                            }
                            field()
                        }
                        if (unit != null && value.isNotEmpty()) {
                            Text(unit, style = MaterialTheme.typography.titleMedium, color = colors.onSurfaceVariant, modifier = Modifier.padding(bottom = 3.dp))
                        }
                    }
                }
            }
        )
        if (hint != null) {
            val (kind, text) = hint
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge.copy(fontSize = 12.5.sp, lineHeight = 16.sp),
                fontWeight = if (kind == HintKind.Secure) FontWeight.SemiBold else FontWeight.Medium,
                color = if (kind == HintKind.Secure) colors.tertiary else colors.primary,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }
    }
}
