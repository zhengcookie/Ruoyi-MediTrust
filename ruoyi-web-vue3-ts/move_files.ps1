# Function to copy files and folders
function Copy-FilesAndFolders {
    param (
        [string]$source,
        [string]$destination
    )

    # Create destination folder if it doesn't exist
    if (-not (Test-Path $destination)) {
        New-Item -ItemType Directory -Path $destination -Force
        Write-Host "Created directory: $destination"
    }

    # Get all items in the source folder
    $items = Get-ChildItem -Path $source

    foreach ($item in $items) {
        $destPath = Join-Path $destination $item.Name

        if ($item.PSIsContainer) {
            # It's a directory, recursively copy
            Copy-FilesAndFolders -source $item.FullName -destination $destPath
        }
        else {
            # It's a file
            if (Test-Path $destPath) {
                # File already exists - create a backup and then copy
                $backupPath = "$destPath.bak"
                Copy-Item -Path $destPath -Destination $backupPath -Force
                Write-Host "Backed up existing file to: $backupPath"
            }
            
            # Copy the file
            Copy-Item -Path $item.FullName -Destination $destPath -Force
            Write-Host "Copied file: $($item.Name) to $destPath"
        }
    }
}

# Define source and destination using full paths to avoid encoding issues
$srcFolderName = "src - 副本"
$source = Join-Path (Get-Location).Path $srcFolderName
$destination = Join-Path (Get-Location).Path "src"

Write-Host "Source path: $source"
Write-Host "Destination path: $destination"

# Check if source directory exists
if (-not (Test-Path $source)) {
    Write-Host "Source directory does not exist: $source"
    exit 1
}

# Check if destination directory exists
if (-not (Test-Path $destination)) {
    Write-Host "Destination directory does not exist: $destination"
    exit 1
}

# Copy special folder and files
Write-Host "Starting copy process from source to destination..."
Copy-FilesAndFolders -source $source -destination $destination

Write-Host "Copy process completed. Please check the application to ensure it runs correctly." 