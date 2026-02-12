/**
 * Metrolist Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.metrolist.music.ui.screens.settings

import android.os.SystemClock
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.metrolist.innertube.YouTube
import com.metrolist.innertube.models.WatchEndpoint
import com.metrolist.music.BuildConfig
import com.metrolist.music.LocalPlayerConnection
import com.metrolist.music.LocalPlayerAwareWindowInsets
import com.metrolist.music.R
import com.metrolist.music.ui.component.IconButton
import com.metrolist.music.ui.utils.backToMain
import com.metrolist.music.models.toMediaMetadata
import com.metrolist.music.playback.queues.YouTubeQueue
import com.metrolist.music.utils.reportException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    val uriHandler = LocalUriHandler.current
    val playerConnection = LocalPlayerConnection.current ?: return
    val coroutineScope = rememberCoroutineScope()
    val versionLabel = if (BuildConfig.DEBUG) {
        stringResource(R.string.debug_build)
    } else {
        BuildConfig.ARCHITECTURE.uppercase(Locale.getDefault())
    }
    val versionDetail = stringResource(
        R.string.about_version_format,
        BuildConfig.VERSION_NAME,
        versionLabel
    )

    fun playVideoById(videoId: String) {
        if (videoId.isBlank()) return
        coroutineScope.launch(Dispatchers.IO) {
            YouTube.queue(listOf(videoId), null).onSuccess { queue ->
                val firstItem = queue.firstOrNull()
                withContext(Dispatchers.Main) {
                    playerConnection.playQueue(
                        YouTubeQueue(
                            WatchEndpoint(videoId = firstItem?.id),
                            firstItem?.toMediaMetadata()
                        )
                    )
                }
            }.onFailure {
                reportException(it)
            }
        }
    }

    val logoTapHandler = rememberMultiTapHandler(requiredTaps = 5) {
        playVideoById("dQw4w9WgXcQ")
    }

    val collaborators = listOf(
        Collaborator(
            name = "MO AGAMY",
            avatarUrl = "https://avatars.githubusercontent.com/u/80542861?v=4",
            profileUrl = "https://github.com/mostafaalagamy",
            favoriteSongVideoId = ""
        ),
        Collaborator(
            name = "Nyx",
            avatarUrl = "https://nyx.meowery.eu/pfp.webp",
            profileUrl = "https://nyx.meowery.eu",
            favoriteSongVideoId = "i8OUh3YvRpk"
        ),
        Collaborator(
            name = "Adriel O'Connel",
            avatarUrl = "https://avatars.githubusercontent.com/u/200536612?v=4",
            profileUrl = "https://github.com/adrielGGmotion",
            favoriteSongVideoId = ""
        ),
        Collaborator(
            name = "Damian Sobczak",
            avatarUrl = "https://avatars.githubusercontent.com/u/56510855?v=4",
            profileUrl = "https://github.com/FullerBread2032",
            favoriteSongVideoId = ""
        )
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(
                LocalPlayerAwareWindowInsets.current.only(
                    WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom
                )
            )
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Spacer(
            Modifier.windowInsetsPadding(
                LocalPlayerAwareWindowInsets.current.only(
                    WindowInsetsSides.Top
                )
            )
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(20.dp))
            Box(
                modifier = Modifier
                    .size(112.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f))
                    .clickable(onClick = logoTapHandler),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.small_icon),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(
                        MaterialTheme.colorScheme.onPrimaryContainer,
                        BlendMode.SrcIn
                    ),
                    modifier = Modifier.size(56.dp)
                )
            }

            Spacer(Modifier.height(10.dp))

            Text(
                text = stringResource(R.string.metrolist_name),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(Modifier.height(18.dp))

        AboutSection(title = null) {
            AboutCard(
                title = stringResource(R.string.metrolist_version),
                description = versionDetail,
                shape = sectionCardShape()
            )
        }

        Spacer(Modifier.height(18.dp))

        AboutSection(title = stringResource(R.string.metrolist_links)) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                AboutCard(
                    title = stringResource(R.string.github),
                    description = stringResource(R.string.metrolist_github_desc),
                    shape = sectionCardShape(),
                    onClick = { uriHandler.openUri("https://github.com/mostafaalagamy") }
                )
                AboutCard(
                    title = stringResource(R.string.buy_me_a_coffee),
                    description = stringResource(R.string.support_metrolist),
                    shape = sectionCardShape(),
                    onClick = { uriHandler.openUri("https://buymeacoffee.com/mostafaalagamy") }
                )
                AboutCard(
                    title = stringResource(R.string.metrolist_website),
                    description = stringResource(R.string.metrolist_website_desc),
                    shape = sectionCardShape(),
                    onClick = { uriHandler.openUri("https://metrolist.meowery.eu") }
                )
            }
        }

        Spacer(Modifier.height(18.dp))

        AboutSection(title = stringResource(R.string.collaborators)) {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                collaborators.chunked(2).forEach { rowItems ->
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (rowItems.size == 1) {
                            Spacer(Modifier.weight(1f))
                        }
                        rowItems.forEach { collaborator ->
                            val onAvatarTap = rememberMultiTapWithSingleHandler(
                                requiredTaps = 5,
                                onSingleTap = { uriHandler.openUri(collaborator.profileUrl) },
                                onTrigger = { playVideoById(collaborator.favoriteSongVideoId) }
                            )
                            CollaboratorItem(
                                name = collaborator.name,
                                avatarUrl = collaborator.avatarUrl,
                                onTap = onAvatarTap,
                                onNameTap = { uriHandler.openUri(collaborator.profileUrl) }
                            )
                        }
                        if (rowItems.size == 1) {
                            Spacer(Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(32.dp))
    }

    TopAppBar(
        title = { Text(stringResource(R.string.about_metrolist_title)) },
        navigationIcon = {
            IconButton(
                onClick = navController::navigateUp,
                onLongClick = navController::backToMain,
            ) {
                Icon(
                    painter = painterResource(R.drawable.arrow_back),
                    contentDescription = null,
                )
            }
        },
        scrollBehavior = scrollBehavior
    )
}

private data class Collaborator(
    val name: String,
    val avatarUrl: String,
    val profileUrl: String,
    val favoriteSongVideoId: String
)

@Composable
private fun AboutSection(
    title: String?,
    content: @Composable () -> Unit
) {
    Column {
        title?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(8.dp))
        }
        content()
    }
}

@Composable
private fun AboutCard(
    title: String,
    description: String,
    shape: Shape,
    onClick: (() -> Unit)? = null
) {
    val colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.32f)
    )

    if (onClick != null) {
        Card(
            onClick = onClick,
            shape = shape,
            colors = colors,
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            AboutCardContent(title = title, description = description)
        }
    } else {
        Card(
            shape = shape,
            colors = colors,
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            AboutCardContent(title = title, description = description)
        }
    }
}

@Composable
private fun AboutCardContent(
    title: String,
    description: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 14.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun CollaboratorItem(
    name: String,
    avatarUrl: String,
    onTap: () -> Unit,
    onNameTap: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onTap)
            .padding(vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(cookieHeptagonShape())
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = avatarUrl,
                contentDescription = name,
                modifier = Modifier
                    .size(56.dp)
                    .clip(cookieHeptagonShape()),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = name,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.clickable(onClick = onNameTap)
        )
    }
}

@Composable
private fun rememberMultiTapHandler(
    requiredTaps: Int,
    timeoutMs: Long = 1200L,
    onTrigger: () -> Unit
): () -> Unit {
    val currentOnTrigger by rememberUpdatedState(onTrigger)
    var tapCount by remember { mutableStateOf(0) }
    var lastTapTime by remember { mutableStateOf(0L) }

    return {
        val now = SystemClock.elapsedRealtime()
        if (now - lastTapTime > timeoutMs) {
            tapCount = 0
        }
        lastTapTime = now
        tapCount += 1
        if (tapCount >= requiredTaps) {
            tapCount = 0
            currentOnTrigger()
        }
    }
}

@Composable
private fun rememberMultiTapWithSingleHandler(
    requiredTaps: Int,
    timeoutMs: Long = 700L,
    onSingleTap: () -> Unit,
    onTrigger: () -> Unit
): () -> Unit {
    val currentOnTrigger by rememberUpdatedState(onTrigger)
    val currentOnSingle by rememberUpdatedState(onSingleTap)
    val scope = rememberCoroutineScope()
    var tapCount by remember { mutableStateOf(0) }
    var lastTapTime by remember { mutableStateOf(0L) }
    var singleTapJob by remember { mutableStateOf<Job?>(null) }

    return {
        val now = SystemClock.elapsedRealtime()
        if (now - lastTapTime > timeoutMs) {
            tapCount = 0
        }
        lastTapTime = now
        tapCount += 1
        singleTapJob?.cancel()
        if (tapCount >= requiredTaps) {
            tapCount = 0
            currentOnTrigger()
        } else {
            singleTapJob = scope.launch {
                delay(timeoutMs)
                if (tapCount == 1) {
                    currentOnSingle()
                }
                tapCount = 0
            }
        }
    }
}

private fun sectionCardShape(): Shape {
    return RoundedCornerShape(24.dp)
}

private fun cookieHeptagonShape(): Shape {
    return GenericShape { size, _ ->
        val radius = min(size.width, size.height) / 2f
        val center = Offset(size.width / 2f, size.height / 2f)
        val points = 7
        val cornerRadius = radius * 0.22f
        val startAngle = -90.0

        val vertices = List(points) { index ->
            val angle = startAngle + index * (360.0 / points)
            Offset(
                x = center.x + radius * cos(Math.toRadians(angle)).toFloat(),
                y = center.y + radius * sin(Math.toRadians(angle)).toFloat()
            )
        }

        fun cornerPoints(prev: Offset, current: Offset, next: Offset): Pair<Offset, Offset> {
            val inDir = (prev - current).normalized()
            val outDir = (next - current).normalized()
            val start = current + inDir.scale(cornerRadius)
            val end = current + outDir.scale(cornerRadius)
            return start to end
        }

        val (firstStart, firstEnd) = cornerPoints(
            prev = vertices.last(),
            current = vertices.first(),
            next = vertices[1]
        )
        moveTo(firstStart.x, firstStart.y)
        quadraticBezierTo(vertices.first().x, vertices.first().y, firstEnd.x, firstEnd.y)

        for (index in 1 until points) {
            val prev = vertices[index - 1]
            val current = vertices[index]
            val next = vertices[(index + 1) % points]
            val (start, end) = cornerPoints(prev, current, next)
            lineTo(start.x, start.y)
            quadraticBezierTo(current.x, current.y, end.x, end.y)
        }
        close()
    }
}

private fun Offset.normalized(): Offset {
    val length = kotlin.math.sqrt(x * x + y * y)
    return if (length == 0f) this else Offset(x / length, y / length)
}

private fun Offset.scale(factor: Float): Offset {
    return Offset(x * factor, y * factor)
}
