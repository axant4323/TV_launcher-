package com.adithyatv.launcher.data

class ReleaseRepository {

    private val releases = listOf(
        // ================= NETFLIX =================
        MediaReleaseItem(
            id = "nf_1",
            title = "Stranger Things",
            appSource = "Netflix",
            appPackage = "com.netflix.ninja",
            releaseYear = "2024",
            durationOrEpisode = "Season 4 • 9 Episodes",
            ratingBadge = "4K Ultra HD • HDR10 • 16+",
            genre = "Sci-Fi • Horror • Mystery",
            synopsis = "When a young boy vanishes, a small town uncovers a mystery involving secret experiments, terrifying supernatural forces and one strange little girl.",
            backdropUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?q=80&w=1600&auto=format&fit=crop",
            posterUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?q=80&w=600&auto=format&fit=crop",
            sourceColor = 0xFFE50914
        ),
        MediaReleaseItem(
            id = "nf_2",
            title = "Squid Game: The Challenge",
            appSource = "Netflix",
            appPackage = "com.netflix.ninja",
            releaseYear = "2024",
            durationOrEpisode = "Season 1 • 10 Episodes",
            ratingBadge = "4K • Dolby Vision • 16+",
            genre = "Reality • Thriller • Competition",
            synopsis = "Immersed in the world of Squid Game, 456 real players put their skills to the ultimate test for a life-changing $4.56 million prize.",
            backdropUrl = "https://images.unsplash.com/photo-1579783900882-c0d3dad7b119?q=80&w=1600&auto=format&fit=crop",
            posterUrl = "https://images.unsplash.com/photo-1579783900882-c0d3dad7b119?q=80&w=600&auto=format&fit=crop",
            sourceColor = 0xFFE50914
        ),
        MediaReleaseItem(
            id = "nf_3",
            title = "Wednesday",
            appSource = "Netflix",
            appPackage = "com.netflix.ninja",
            releaseYear = "2024",
            durationOrEpisode = "Season 1 • 8 Episodes",
            ratingBadge = "4K UHD • 13+",
            genre = "Comedy • Crime • Fantasy",
            synopsis = "Smart, sarcastic and a little dead inside, Wednesday Addams investigates a murder spree while making new friends — and foes — at Nevermore Academy.",
            backdropUrl = "https://images.unsplash.com/photo-1509281373149-e957c6296406?q=80&w=1600&auto=format&fit=crop",
            posterUrl = "https://images.unsplash.com/photo-1509281373149-e957c6296406?q=80&w=600&auto=format&fit=crop",
            sourceColor = 0xFFE50914
        ),

        // ================= PRIME VIDEO =================
        MediaReleaseItem(
            id = "pv_1",
            title = "Mirzapur",
            appSource = "Prime Video",
            appPackage = "com.amazon.amazonvideo.livingroom",
            releaseYear = "2024",
            durationOrEpisode = "Season 3 • 10 Episodes",
            ratingBadge = "4K UHD • HDR • 18+",
            genre = "Action • Crime • Drama",
            synopsis = "With the throne of Purvanchal up for grabs, Guddu Pandit and Golu confront new contenders in a fierce and ruthless battle for supremacy.",
            backdropUrl = "https://images.unsplash.com/photo-1536440136628-849c177e76a1?q=80&w=1600&auto=format&fit=crop",
            posterUrl = "https://images.unsplash.com/photo-1536440136628-849c177e76a1?q=80&w=600&auto=format&fit=crop",
            sourceColor = 0xFF00A8E1
        ),
        MediaReleaseItem(
            id = "pv_2",
            title = "The Boys",
            appSource = "Prime Video",
            appPackage = "com.amazon.amazonvideo.livingroom",
            releaseYear = "2024",
            durationOrEpisode = "Season 4 • 8 Episodes",
            ratingBadge = "4K UHD • Dolby Atmos • 18+",
            genre = "Action • Comedy • Sci-Fi",
            synopsis = "The world is on the brink. Victoria Neuman is closer than ever to the Oval Office and under the muscled thumb of Homelander, who is consolidating his power.",
            backdropUrl = "https://images.unsplash.com/photo-1563089145-599997674d42?q=80&w=1600&auto=format&fit=crop",
            posterUrl = "https://images.unsplash.com/photo-1563089145-599997674d42?q=80&w=600&auto=format&fit=crop",
            sourceColor = 0xFF00A8E1
        ),
        MediaReleaseItem(
            id = "pv_3",
            title = "Panchayat",
            appSource = "Prime Video",
            appPackage = "com.amazon.amazonvideo.livingroom",
            releaseYear = "2024",
            durationOrEpisode = "Season 3 • 8 Episodes",
            ratingBadge = "4K UHD • U/A 13+",
            genre = "Comedy • Drama",
            synopsis = "An engineering graduate takes up a job as a panchayat secretary in a remote village Phulera, navigating rural politics and heartfelt community bonds.",
            backdropUrl = "https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?q=80&w=1600&auto=format&fit=crop",
            posterUrl = "https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?q=80&w=600&auto=format&fit=crop",
            sourceColor = 0xFF00A8E1
        ),

        // ================= DISNEY+ HOTSTAR =================
        MediaReleaseItem(
            id = "hs_1",
            title = "Deadpool & Wolverine",
            appSource = "Disney+ Hotstar",
            appPackage = "in.startv.hotstar",
            releaseYear = "2024",
            durationOrEpisode = "2h 8m",
            ratingBadge = "IMAX Enhanced • Dolby Vision • 16+",
            genre = "Action • Comedy • Sci-Fi",
            synopsis = "A listless Wade Wilson toils away in civilian life with his days as the morally flexible mercenary behind him, until the Time Variance Authority pulls him into a multiversal mission.",
            backdropUrl = "https://images.unsplash.com/photo-1607604276583-eef5d076aa5f?q=80&w=1600&auto=format&fit=crop",
            posterUrl = "https://images.unsplash.com/photo-1607604276583-eef5d076aa5f?q=80&w=600&auto=format&fit=crop",
            sourceColor = 0xFF0F75BD
        ),
        MediaReleaseItem(
            id = "hs_2",
            title = "Shōgun",
            appSource = "Disney+ Hotstar",
            appPackage = "in.startv.hotstar",
            releaseYear = "2024",
            durationOrEpisode = "Season 1 • 10 Episodes",
            ratingBadge = "4K HDR • Emmy Winner • 18+",
            genre = "Historical • Drama • War",
            synopsis = "In Japan in the year 1600, Lord Yoshii Toranaga must fight for his life as his enemies on the Council of Regents unite against him.",
            backdropUrl = "https://images.unsplash.com/photo-1528164344705-475426879c0d?q=80&w=1600&auto=format&fit=crop",
            posterUrl = "https://images.unsplash.com/photo-1528164344705-475426879c0d?q=80&w=600&auto=format&fit=crop",
            sourceColor = 0xFF0F75BD
        ),

        // ================= SONY LIV & AIRTEL XSTREAM =================
        MediaReleaseItem(
            id = "sl_1",
            title = "Scam 2003: The Telgi Story",
            appSource = "Sony LIV",
            appPackage = "com.sonyliv",
            releaseYear = "2024",
            durationOrEpisode = "Season 2 • 10 Episodes",
            ratingBadge = "Full HD • 5.1 Audio • 16+",
            genre = "Biography • Crime • Drama",
            synopsis = "Born in Khanapur in Karnataka, Abdul Karim Telgi became the mastermind behind one of India's most ingenious stamp paper scams.",
            backdropUrl = "https://images.unsplash.com/photo-1590283603385-17ffb3a7f29f?q=80&w=1600&auto=format&fit=crop",
            posterUrl = "https://images.unsplash.com/photo-1590283603385-17ffb3a7f29f?q=80&w=600&auto=format&fit=crop",
            sourceColor = 0xFF8A2BE2
        ),
        MediaReleaseItem(
            id = "sl_2",
            title = "Maharani",
            appSource = "Sony LIV",
            appPackage = "com.sonyliv",
            releaseYear = "2024",
            durationOrEpisode = "Season 3 • 8 Episodes",
            ratingBadge = "Full HD • 16+",
            genre = "Political Drama • Thriller",
            synopsis = "Rani Bharti prepares for her biggest political and personal battle as she seeks justice and retribution against seasoned opponents.",
            backdropUrl = "https://images.unsplash.com/photo-1514565131-fce0801e5785?q=80&w=1600&auto=format&fit=crop",
            posterUrl = "https://images.unsplash.com/photo-1514565131-fce0801e5785?q=80&w=600&auto=format&fit=crop",
            sourceColor = 0xFF8A2BE2
        ),

        // ================= JIOCINEMA =================
        MediaReleaseItem(
            id = "jc_1",
            title = "Asur: Welcome to Your Dark Side",
            appSource = "JioCinema",
            appPackage = "com.jio.media.stb.ondemand",
            releaseYear = "2024",
            durationOrEpisode = "Season 2 • 8 Episodes",
            ratingBadge = "4K UHD • 16+",
            genre = "Mythology • Crime • Psychological Thriller",
            synopsis = "A unique psychological thriller that pits two opposing worlds against each other: forensic science and the deep mysticism of ancient Indian mythology.",
            backdropUrl = "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?q=80&w=1600&auto=format&fit=crop",
            posterUrl = "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?q=80&w=600&auto=format&fit=crop",
            sourceColor = 0xFFD81B60
        ),
        MediaReleaseItem(
            id = "jc_2",
            title = "Taali",
            appSource = "JioCinema",
            appPackage = "com.jio.media.stb.ondemand",
            releaseYear = "2024",
            durationOrEpisode = "Miniseries • 6 Episodes",
            ratingBadge = "Full HD • 13+",
            genre = "Biographical Drama",
            synopsis = "The courageous journey of transgender activist Gauri Sawant and her fight for recognition and constitutional rights.",
            backdropUrl = "https://images.unsplash.com/photo-1492684223066-81342ee5ff30?q=80&w=1600&auto=format&fit=crop",
            posterUrl = "https://images.unsplash.com/photo-1492684223066-81342ee5ff30?q=80&w=600&auto=format&fit=crop",
            sourceColor = 0xFFD81B60
        ),

        // ================= AIRTEL XSTREAM =================
        MediaReleaseItem(
            id = "ax_1",
            title = "Airtel Xstream Live Sports & Movies",
            appSource = "Airtel Xstream",
            appPackage = "tv.airtel.xstream.tvapp",
            releaseYear = "2024",
            durationOrEpisode = "Live Channels • 4K",
            ratingBadge = "Live HD • 5.1 Surround",
            genre = "Live TV • Sports • Regional Hits",
            synopsis = "Stream live sports tournaments, exclusive regional cinema, and premium channel feeds in crisp high definition.",
            backdropUrl = "https://images.unsplash.com/photo-1461896836934-ffe607ba8211?q=80&w=1600&auto=format&fit=crop",
            posterUrl = "https://images.unsplash.com/photo-1461896836934-ffe607ba8211?q=80&w=600&auto=format&fit=crop",
            sourceColor = 0xFFED1C24
        ),

        // ================= YOUTUBE =================
        MediaReleaseItem(
            id = "yt_1",
            title = "4K Cinematic Wildlife & Nature Odyssey",
            appSource = "YouTube",
            appPackage = "com.google.android.youtube.tv",
            releaseYear = "2024",
            durationOrEpisode = "45 mins",
            ratingBadge = "4K 60FPS • HDR • All Ages",
            genre = "Documentary • Nature • 4K HDR",
            synopsis = "Experience the world's most breathtaking untouched landscapes, coral reefs, and arctic wildlife in glorious high dynamic range.",
            backdropUrl = "https://images.unsplash.com/photo-1470071459604-3b5ec3a7fe05?q=80&w=1600&auto=format&fit=crop",
            posterUrl = "https://images.unsplash.com/photo-1470071459604-3b5ec3a7fe05?q=80&w=600&auto=format&fit=crop",
            sourceColor = 0xFFFF0000
        )
    )

    fun getAllReleases(): List<MediaReleaseItem> = releases

    fun getSpotlightReleases(): List<MediaReleaseItem> = listOf(
        releases[0], // Stranger Things (Netflix)
        releases[3], // Mirzapur (Prime)
        releases[6], // Deadpool & Wolverine (Hotstar)
        releases[8], // Scam 2003 (Sony LIV)
        releases[10] // Wildlife Odyssey (YouTube)
    )

    fun getReleasesBySource(appSource: String): List<MediaReleaseItem> {
        return releases.filter { it.appSource.equals(appSource, ignoreCase = true) }
    }

    fun getReleasesByPackage(packageName: String): List<MediaReleaseItem> {
        return releases.filter { it.appPackage == packageName }
    }
}
