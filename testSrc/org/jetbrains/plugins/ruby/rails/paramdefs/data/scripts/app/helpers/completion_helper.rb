module CompletionHelper
    def test()
        ActionView::Helpers::AssetTagHelper::javascript_include_tag('')
#                                                                    <caret>
    end
end