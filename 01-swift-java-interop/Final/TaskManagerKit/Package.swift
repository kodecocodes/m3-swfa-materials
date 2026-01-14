// swift-tools-version: 6.2
import PackageDescription

let package = Package(
    name: "TaskManagerKit",
    products: [
        .library(
            name: "TaskManagerKit",
            type: .dynamic,
            targets: ["TaskManagerKit"]
        ),
    ],
    targets: [
        .target(
            name: "TaskManagerKit",
            dependencies: []
        ),
    ]
)
