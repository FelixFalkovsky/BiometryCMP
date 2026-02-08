import SwiftUI
import ComposeApp

struct ContentView: View {
    var body: some View {
        ComposeHostView()
            .frame(maxWidth: .infinity, maxHeight: .infinity)
            .ignoresSafeArea()
    }
}

struct ComposeHostView: UIViewControllerRepresentable {
    @MainActor
    func makeUIViewController(context: Context) -> UIViewController {
        MainKt.BiometrySampleViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
