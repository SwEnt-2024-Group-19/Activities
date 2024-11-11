package com.android.sample.model.map

class MockLocationPermissionChecker(private var permissionGranted: Boolean) : PermissionChecker {

    override fun hasLocationPermission(): Boolean {
        // Return the mock value instead of actually checking the permission
        return permissionGranted
    }

    // Method to update the mock result for testing
    fun setPermissionGranted(granted: Boolean) {
        permissionGranted = granted
    }
}
